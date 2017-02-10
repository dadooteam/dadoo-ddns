var cron = require('cron')
var request = require('request')

const ID = 24476
const TOKEN = '36ec5ab736e25a3ae7c66123cc20a822'
const DOMAIN = 'dadoo.im'
const RECORDS = new Set(['test'])

const IP_URL = 'http://cgi1.apnic.net/cgi-bin/my-ip.php'
const DOMAIN_LIST_URL = 'https://dnsapi.cn/Domain.List'
const RECORD_LIST_URL = 'https://dnsapi.cn/Record.List'
const DDNS_UPDATE_URL = 'https://dnsapi.cn/Record.Ddns'

let json = true
let headers = {
    'User-Agent': 'Dadoo DDNS/2.0.0 (codekitten@qq.com)'
}
let form = {
    login_token: `${ID},${TOKEN}`,
    format: 'json'
}
let ip = ''

let cronJob = new cron.CronJob('0 * * * * *', () => {
    console.log('本轮动态域名解析开始')
    new Promise((resolve, reject) => {
        request.get(IP_URL, {json}, (error, response, body) => {
            if (!error && response.statusCode === 200) {
                resolve(body)
            } else {
                reject(error)
            }
        })
    }).then((body) => {
        ip = eval(body)
        console.log(`ip地址为${ip}`)
        return new Promise((resolve, reject) => {
            request.post(DOMAIN_LIST_URL, {headers, form, json}, (error, response, body) => {
                if (!error && response.statusCode === 200) {
                    resolve(body)
                } else {
                    reject(error)
                }
            })
        })
    }, (error) => {
        console.log(error)
    }).then((body) => {
        for (let domain of body.domains) {
            if (domain.name === DOMAIN) {
                form['domain_id'] = domain.id
                return new Promise((resolve, reject) => {
                    request.post(RECORD_LIST_URL, {headers, form, json}, (error, response, body) => {
                        if (!error && response.statusCode === 200) {
                            resolve(body)
                        } else {
                            reject(error)
                        }
                    })
                })
            }
        }
    }, (error) => {
        console.log(error)
    }).then((body) => {
        let plist = []
        for (let record of body.records) {
            if (RECORDS.has(record.name) && record.value !== ip) {
                form['record_id'] = record.id
                form['sub_domain'] = record.name
                form['record_line'] = record.line
                form['record_line_id'] = record['line_id']
                form['value'] = ip
                form['ttl'] = '10'
                plist.push(new Promise((resolve, reject) => {
                    request.post(DDNS_UPDATE_URL, {headers, form}, (error, response, body) => {
                        if (!error && response.statusCode === 200) {
                            let name = record.name
                            console.log(`${name}更新ip成功，当前ip为${ip}`)
                            resolve(body)
                        } else {
                            console.error(`更新${name}的ip地址失败`)
                            reject(error)
                        }
                    })
                }))
            }
        }
        return Promise.all(plist)
    }, (error) => {
        console.log(error)
    }).then((body) => {
        console.log(body)
        console.log('本轮动态域名解析成功')
    }, (error) => {
        console.log(error)
        console.log('本轮动态域名解析失败')
    })
})
cronJob.start()