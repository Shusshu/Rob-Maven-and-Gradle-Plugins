package be.billington.rob.bitbucket;

import java.util.List;

public class BitbucketResponse {

    private int pagelen;
    private List<Commit> values;
    private int page;
    private String next;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public int getPagelen() {
        return pagelen;
    }

    public void setPagelen(int pagelen) {
        this.pagelen = pagelen;
    }

    public List<Commit> getValues() {
        return values;
    }

    public void setValues(List<Commit> values) {
        this.values = values;
    }
}
