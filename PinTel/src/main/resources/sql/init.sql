create table TgUser (
    userId integer primary key,
    username varchar(64) not null,
    chatId varchar(32) not null,
    selectionType varchar(32);
    lastCommand varchar(64)
);