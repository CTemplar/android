package mobileapp.ctemplar.com.ctemplarapp.repository.dto.domains;

import mobileapp.ctemplar.com.ctemplarapp.net.response.domains.CustomDomainsResponse;

public class CustomDomainsDTO {
    private int totalCount;
    private int pageCount;
    private boolean next;
    private boolean previous;
    private CustomDomainDTO[] results;

    public CustomDomainsDTO() {
    }

    public CustomDomainsDTO(int totalCount, int pageCount, boolean next, boolean previous, CustomDomainDTO[] results) {
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

    public CustomDomainDTO[] getResults() {
        return results;
    }

    public void setResults(CustomDomainDTO[] results) {
        this.results = results;
    }

    public static CustomDomainsDTO get(CustomDomainsResponse response) {
        return new CustomDomainsDTO(
                response.getTotalCount(),
                response.getPageCount(),
                response.isNext(),
                response.isPrevious(),
                CustomDomainDTO.get(response.getResults())
        );
    }
}
