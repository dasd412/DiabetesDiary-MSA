package com.dasd412.api.diaryservice.domain;

public interface StringMaxLength {
    /*
    도메인 엔티티에서 문자열 칼럼을 쓸 경우,
    최대 길이를 이곳에 지정해 놓자. 매직 넘버 제거용.
     */
    int DIARY_REMARK=500;
    int FOOD_NAME=50;
}
