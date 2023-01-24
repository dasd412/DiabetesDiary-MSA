package com.dasd412.api.readdiaryservice.adapter.out.web;

import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FoodPageMaker<T> {

    /**
     * 화면에 출력할 결과
     */
    private final Page<T> foodPage;

    private Pageable previousPage;

    private Pageable nextPage;

    /**
     * 브라우저 화면에 표시되는, 1부터 시작하는 페이지 번호를 나타낸다.
     */
    private final int currentPageNumber;

    private final int totalPageCount;

    /**
     * 현재 페이지 정보를 갖고 있는 객체
     */
    private final Pageable currentPage;

    /**
     * 페이지 번호의 시작부터 끝까지의 페이징 객체들을 저장한 리스트
     */
    private final List<Pageable> pageableList;

    /**
     * @param foodPage 화면에 출력할 결과
     */
    public FoodPageMaker(Page<T> foodPage) {
        this.foodPage = foodPage;

        this.currentPage = foodPage.getPageable();

        /*
         pageable 객체의 getPageNumber()는 0부터 시작하지만,
         브라우저에서는 1부터 시작해야 하기 때문에
         다음과 같은 처리를 하였다.
        */
        this.currentPageNumber = currentPage.getPageNumber() + 1;

        this.totalPageCount = foodPage.getTotalPages();

        this.pageableList = new ArrayList<>();

        calculatePages();
    }

    private void calculatePages() {
        /*
            math.ceil()은 소수점 이하를 올리는 static method다. 예를 들어 보자.
            현재 페이지 번호가 1 ~ 10 중 하나라고 해보자. 그러면 endNumber = 10, startNumber = 10 - 9 = 1이 된다.
            11 ~ 20 일 경우에는 소수점 올림에 의해 endNumber = 20, startNumber = 20 - 9 = 11이 된다.
         */
        int endNumber = (int) (Math.ceil(this.currentPageNumber / 10.0) * 10);
        int statNumber = endNumber - 9;

        /*
           previousOrFirst()는 바로 이전 페이지로만 이동 가능하다.
           따라서 맨 왼쪽 페이지로 시작 커서를 옮기고 싶다면, 현재 페이지 번호 - 시작 페이지 번호 차이 만큼 왼쪽으로 이동해야 한다.
         */
        Pageable startPage = this.currentPage;

        for (int i = statNumber; i < this.currentPageNumber; i++) {
            startPage = startPage.previousOrFirst();
        }

        /*
            맨 왼쪽 페이지로 커서를 옮긴 후, 해당 커서의 페이지 번호가 0이라면 이전 페이지는 존재하지 않으므로 null 처리한다.
            그렇지 않으면 이전 페이지가 존재하는 것이므로 이전 페이지를 설정한다.
            (ex) startPage = 10이면 previousPage는 9가 되야 한다.
         */
        this.previousPage = startPage.getPageNumber() <= 0 ? null : startPage.previousOrFirst();

        /*
            만약 endNumber가 총 페이지 개수보다 크다면, 다음 페이지를 null로 만들어야 한다.
            예를 들어 보자.
            총 페이지 개수는 7개, 즉 1 ~ 7 만 있다. 그리고 계산 식에 의해 endNumber = 10이 된다.
            이런 상황에서는 10 페이지까지가 아닌 7 페이지까지만 pageableList에 담아야 한다.
            그리고 11 페이지도 필요없으므로 nextPage = null.
         */
        if (this.totalPageCount < endNumber) {
            endNumber = this.totalPageCount;
            this.nextPage = null;
        }

        /*
            왼쪽으로 옮겨진 커서에 있는 시작 페이지부터 시작하여
            끝 페이지까지 순서대로 순회하면서 pageableList에 담아준다.
         */
        for (int i = statNumber; i <= endNumber; i++) {
            this.pageableList.add(startPage);
            startPage = startPage.next();
        }

        /*
            반복문을 마치고 나온 startPage는 endNumber + 1 에 해당하는 페이지를 가리키고 있다.
            예를 들어 endNumber = 10이면 startPage = 11을 가리키고 있다.
            만약 페이지 전체 개수가 11보다 크거나 같다면 다음 페이지는 존재하지 않으므로 null 처리한다.
            그렇지 않은 경우에는 nextPage가 startPage를 가리키도록 설정한다.
         */
        this.nextPage = startPage.getPageNumber() + 1 < totalPageCount ? startPage : null;

    }
}
