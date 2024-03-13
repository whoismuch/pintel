create table TgUser (
    userId integer primary key,
    username varchar(64) not null,
    name varchar(64),
    chatId varchar(32) not null,
    lastMessage varchar(200)
);