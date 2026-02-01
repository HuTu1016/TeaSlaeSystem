package org.example.teasystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * AI智能问答控制器
 * 调用阿里云千问模型，提供茶叶购买咨询服务
 */
@Tag(name = "AI智能问答")
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiChatController {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final String CHAT_HISTORY_KEY_PREFIX = "ai:chat:history:";

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.model:qwen-max}")
    private String model;

    @Value("${ai.base-url:https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String SYSTEM_PROMPT = """
        你是茗韵茶业的智能茶叶顾问"茶小韵"，专门为顾客提供茶叶购买咨询服务。
        
        你的职责：
        1. 根据顾客的口味偏好、预算、送礼需求等，推荐合适的茶叶
        2. 介绍各类茶叶的特点、功效、冲泡方法
        3. 解答关于茶叶保存、品鉴的问题
        4. 引导顾客了解我们的产品和优惠活动
        
        茶叶分类知识：
        - 绿茶：龙井、碧螺春、毛峰，清新爽口，适合春夏
        - 红茶：正山小种、金骏眉、祁门红茶，醇厚甘甜，适合秋冬
        - 乌龙茶：铁观音、大红袍、凤凰单丛，香气馥郁
        - 白茶：白毫银针、白牡丹，清淡养生
        - 普洱茶：生普、熟普，适合收藏和日常饮用
        
        回答要求：
        - 语气亲切友好，像朋友聊天
        - 回答简洁明了，不超过200字
        - 适时推荐我们商城的茶叶产品
        - 可以询问顾客更多信息以便更好推荐
        """;

    @Operation(summary = "AI问答")
    @PostMapping("/chat")
    public Result<ChatResponse> chat(@RequestBody ChatRequest request) {
        if (apiKey == null || apiKey.isEmpty()) {
            // 如果没有配置API Key，返回模拟回复
            return Result.success(mockReply(request.getMessage()));
        }

        try {
            // 构建请求
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("model", model);

            List<Map<String, String>> messages = new ArrayList<>();
            
            // 系统提示词
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", SYSTEM_PROMPT);
            messages.add(systemMsg);

            // 历史消息
            if (request.getHistory() != null) {
                for (ChatMessage msg : request.getHistory()) {
                    Map<String, String> m = new HashMap<>();
                    m.put("role", msg.getRole());
                    m.put("content", msg.getContent());
                    messages.add(m);
                }
            }

            // 当前消息
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", request.getMessage());
            messages.add(userMsg);

            body.put("messages", messages);
            body.put("max_tokens", 500);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map responseBody = response.getBody();
                List<Map> choices = (List<Map>) responseBody.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map message = (Map) choices.get(0).get("message");
                    String content = (String) message.get("content");
                    
                    ChatResponse chatResponse = new ChatResponse();
                    chatResponse.setReply(content);
                    return Result.success(chatResponse);
                }
            }

            return Result.success(mockReply(request.getMessage()));

        } catch (Exception e) {
            e.printStackTrace();
            return Result.success(mockReply(request.getMessage()));
        }
    }

    /**
     * 模拟回复（当API不可用时）
     */
    private ChatResponse mockReply(String message) {
        ChatResponse response = new ChatResponse();
        String reply;

        if (message.contains("推荐") || message.contains("买什么")) {
            reply = "您好！很高兴为您推荐茶叶~ 请问您是自己喝还是送人呢？平时喜欢什么口味的茶？比如清淡的绿茶、醇厚的红茶，还是香气浓郁的乌龙茶？告诉我您的偏好，我来为您推荐最合适的~";
        } else if (message.contains("绿茶")) {
            reply = "绿茶是不发酵茶，保留了茶叶的天然物质，清新爽口。推荐您试试我们的明前龙井，产自西湖核心产区，鲜爽回甘，很适合春夏饮用哦~";
        } else if (message.contains("红茶")) {
            reply = "红茶温润养胃，秋冬饮用最佳。我们家的金骏眉和正山小种都很受欢迎，蜜香浓郁，口感醇厚。要不要看看详情？";
        } else if (message.contains("送礼") || message.contains("礼盒")) {
            reply = "送礼选茶叶很有品味！我们有精美的礼盒装，大红袍、金骏眉礼盒都很适合。包装精美，档次高，送长辈或商务伙伴都很合适~";
        } else if (message.contains("价格") || message.contains("多少钱")) {
            reply = "我们的茶叶价格从几十到几百不等，能满足不同需求。您可以在商城看看具体产品，也可以告诉我您的预算，我帮您推荐性价比高的~";
        } else if (message.contains("冲泡") || message.contains("怎么泡")) {
            reply = "冲泡茶叶有讲究哦~一般来说：绿茶用80-85°C水，红茶用90-95°C水，乌龙茶用沸水。投茶量大概是茶具的1/3。第一泡可以快速出汤，后面慢慢延长时间~";
        } else {
            reply = "您好！我是茶小韵，茗韵茶业的智能顾问~有什么关于茶叶的问题都可以问我哦！比如想买什么茶、茶叶怎么挑选、怎么冲泡等等~";
        }

        response.setReply(reply);
        return response;
    }

    /**
     * 保存对话历史到Redis
     */
    @Operation(summary = "保存对话历史")
    @PostMapping("/history/save")
    public Result<Void> saveHistory(@RequestBody SaveHistoryRequest request) {
        try {
            Long userId = SecurityUtils.getCurrentUserIdOrNull();
            String today = LocalDate.now().toString();
            String key = CHAT_HISTORY_KEY_PREFIX + userId + ":" + today;
            
            String json = objectMapper.writeValueAsString(request.getMessages());
            stringRedisTemplate.opsForValue().set(key, json);
            
            // 设置过期时间到第二天凌晨
            long secondsUntilMidnight = ChronoUnit.SECONDS.between(
                java.time.LocalDateTime.now(),
                LocalDate.now().plusDays(1).atStartOfDay()
            );
            stringRedisTemplate.expire(key, secondsUntilMidnight + 3600, TimeUnit.SECONDS); // 多1小时缓冲
            
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("保存对话历史失败");
        }
    }
    
    /**
     * 获取今日对话历史
     */
    @Operation(summary = "获取对话历史")
    @GetMapping("/history")
    public Result<List<ChatMessage>> getHistory() {
        try {
            Long userId = SecurityUtils.getCurrentUserIdOrNull();
            String today = LocalDate.now().toString();
            String key = CHAT_HISTORY_KEY_PREFIX + userId + ":" + today;
            
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null || json.isEmpty()) {
                return Result.success(new ArrayList<>());
            }
            
            List<ChatMessage> messages = objectMapper.readValue(json, new TypeReference<List<ChatMessage>>() {});
            return Result.success(messages);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.success(new ArrayList<>());
        }
    }
    
    /**
     * 清空对话历史
     */
    @Operation(summary = "清空对话历史")
    @DeleteMapping("/history")
    public Result<Void> clearHistory() {
        try {
            Long userId = SecurityUtils.getCurrentUserIdOrNull();
            String today = LocalDate.now().toString();
            String key = CHAT_HISTORY_KEY_PREFIX + userId + ":" + today;
            stringRedisTemplate.delete(key);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("清空对话历史失败");
        }
    }

    @Data
    public static class ChatRequest {
        private String message;
        private List<ChatMessage> history;
    }

    @Data
    public static class ChatMessage {
        private String role;
        private String content;
    }

    @Data
    public static class ChatResponse {
        private String reply;
    }
    
    @Data
    public static class SaveHistoryRequest {
        private List<ChatMessage> messages;
    }
}
