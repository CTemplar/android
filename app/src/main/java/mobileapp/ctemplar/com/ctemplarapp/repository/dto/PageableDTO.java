package mobileapp.ctemplar.com.ctemplarapp.repository.dto;

import java.util.List;

public class PageableDTO<T> {
    private int totalCount;
    private int pageCount;
    private boolean next;
    private boolean previous;
    private List<T> results;

    public PageableDTO() {
    }

    public PageableDTO(int totalCount, int pageCount, boolean next, boolean previous, List<T> results) {
        this.totalCount = totalCount;
        this.pageCount = pageCount;
        this.next = next;
        this.previous = previous;
        this.results = results;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public boolean isNext() {
        return next;
    }

    public void setNext(boolean next) {
        this.next = next;
    }

    public boolean isPrevious() {
        return previous;
    }

    public void setPrevious(boolean previous) {
        this.previous = previous;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}
