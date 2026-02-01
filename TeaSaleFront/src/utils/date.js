/**
 * 格式化日期时间
 * @param {string|Date} time 
 * @param {string} format 默认为 YYYY-MM-DD HH:mm:ss
 * @returns {string}
 */
export function formatDate(time, format = 'YYYY-MM-DD HH:mm:ss') {
    if (!time) return ''
    const date = new Date(time)

    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hour = String(date.getHours()).padStart(2, '0')
    const minute = String(date.getMinutes()).padStart(2, '0')
    const second = String(date.getSeconds()).padStart(2, '0')

    // 简单实现替换，实际项目可能用 dayjs
    // 这里为了适配简单需求，只做简单替换
    if (format === 'YYYY-MM-DD') {
        return `${year}-${month}-${day}`
    }

    return `${year}-${month}-${day} ${hour}:${minute}:${second}`
}
