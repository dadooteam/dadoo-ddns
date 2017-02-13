var log4js = require('log4js')

log4js.configure({
    appenders: [
        {
            type: 'console'
        }
    ],
    level: log4js.levels.INFO
})

module.exports = {
    ID: 24476,
    TOKEN: '36ec5ab736e25a3ae7c66123cc20a822',
    DOMAIN: 'dadoo.im',
    RECORDS: new Set(['db', 'api']),
    IP_URL: 'http://cgi1.apnic.net/cgi-bin/my-ip.php',
    DOMAIN_LIST_URL: 'https://dnsapi.cn/Domain.List',
    RECORD_LIST_URL: 'https://dnsapi.cn/Record.List',
    DDNS_UPDATE_URL: 'https://dnsapi.cn/Record.Ddns',
    HEADER: {
        'User-Agent': 'Dadoo DDNS/2.0.0 (codekitten@qq.com)'
    },
    CRON: '0 */5 * * * *',
    LoggerFactory: log4js
}