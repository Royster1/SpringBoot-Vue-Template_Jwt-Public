package com.example.entity.vo.response;


import lombok.Data;

import java.util.Date;

// 前端交互是VO, 跟数据库交互的DTO
@Data
public class AuthorizeVO {
    String username;
    String role;
    String token;
    Date expire;
}
