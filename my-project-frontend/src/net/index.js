// axios请求封装
import axios from 'axios'
import {ElMessage} from "element-plus";

// 验证itemnamme
const authItemName = "access_token"

const defaultFailure = (message, code, url) => {
    console.warn(`请求地址: ${url}, 状态码: ${code}, 错误信息: ${message}`)
    ElMessage.warning(message)
}

const defaultError = (error) => {
    console.error(error)
    ElMessage.error('发生了一些错误，请联系管理员')
}

// 对token的操作
//保存token
function storeAccessToken(token, remember, expire) {
    const authObj = {token: token, expire: expire}
    const str = JSON.stringify(authObj)
    if (remember)
        localStorage.setItem(authItemName, str)
    else
        sessionStorage.setItem(authItemName, str)
}

// 获取token
function takeAccessToken() {
    const str = localStorage.getItem(authItemName) || sessionStorage.getItem(authItemName)
    // 没有拿到token说明用户没有登录
    if (!str) return null;
    const authObj = JSON.parse(str)
    // 如果过期了
    if (authObj.expire <= new Date()) {
        // 删除token
        deleteAccessToken()
        ElMessage.warning('登录状态已过期,请重新登录')
    }
    return authObj.token
}

// 删除token
function deleteAccessToken() {
    localStorage.removeItem(authItemName)
    sessionStorage.removeItem(authItemName)
}

function internalPost(url, data, headers, success, failure, error = defaultError){
    axios.post(url, data, { headers: headers }).then(({data}) => {
        if(data.code === 200)
            success(data.data)
        else
            failure(data.message, data.code, url)
    }).catch(err => error(err))
}


function internalGet(url, headers, success, failure, error = defaultError){
    axios.get(url, { headers: headers }).then(({data}) => {
        if(data.code === 200)
            success(data.data)
        else
            failure(data.message, data.code, url)
    }).catch(err => error(err))
}


function login(username, password, remember, success, failure = defaultFailure()) {
    internalPost('/api/auth/login', {
        username: username,
        password: password
    }, {
        // 以表单的形式发送
        'Content-Type': 'application/x-www-form-urlencoded'
    }, (data) => {
        storeAccessToken(remember, data.token, data.expire)
        ElMessage.success(`登录成功, 欢迎${data.username}来到我们的系统`)
        success(data)
    }, () => {

    })
}

export {login}
