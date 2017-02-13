var cron = require('cron')
var request = require('request')
var context = require('./context')

let json = true
let form = {
    login_token: `${context.ID},${context.TOKEN}`,
    format: 'json'
}
let ip = ''
const LOGGER = context.LoggerFactory.getLogger('index')

let cronJob = new cron.CronJob(context.CRON, () => {
    LOGGER.info('本轮动态域名解析开始')
    new Promise((resolve, reject) => {
        request.get(context.IP_URL, {json}, (error, response, body) => {
            if (!error && response.statusCode === 200) {
                resolve(body)
            } else {
                reject(error)
            }
        })
    }).then(body => {
        ip = eval(body)
        LOGGER.info(`ip地址为${ip}`)
        return new Promise((resolve, reject) => {
            request.post(context.DOMAIN_LIST_URL, {headers: context.HEADER, form, json}, (error, response, body) => {
                if (!error && response.statusCode === 200) {
                    resolve(body)
                } else {
                    reject(error)
                }
            })
        })
    }, (error) => {
        LOGGER.error(error)
    }).then((body) => {
        for (let domain of body.domains) {
            if (domain.name === context.DOMAIN) {
                form['domain_id'] = domain.id
                return new Promise((resolve, reject) => {
                    request.post(context.RECORD_LIST_URL, {headers: context.HEADER, form, json}, (error, response, body) => {
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
        LOGGER.error(error)
    }).then((body) => {
        let plist = []
        for (let record of body.records) {
            if (context.RECORDS.has(record.name) && record.value !== ip) {
                form['record_id'] = record.id
                form['sub_domain'] = record.name
                form['record_line'] = record.line
                form['record_line_id'] = record['line_id']
                form['value'] = ip
                form['ttl'] = '10'
                plist.push(new Promise((resolve, reject) => {
                    request.post(context.DDNS_UPDATE_URL, {headers: context.HEADER, form}, (error, response, body) => {
                        if (!error && response.statusCode === 200) {
                            let name = record.name
                            LOGGER.info(`${name}更新ip成功，当前ip为${ip}`)
                            resolve(body)
                        } else {
                            LOGGER.error(`更新${name}的ip地址失败`)
                            reject(error)
                        }
                    })
                }).then(
                    name => LOGGER.info(`${name}更新ip成功，当前ip为${ip}`), 
                    name => LOGGER.error(`更新${name}的ip地址失败`)
                ))
            }
        }
        return Promise.all(plist)
    }, (error) => {
        LOGGER.error(error)
    }).then((body) => {
        LOGGER.info('本轮动态域名解析成功')
    }, (error) => {
        LOGGER.error(error)
        LOGGER.error('本轮动态域名解析失败')
    })
})
cronJob.start()