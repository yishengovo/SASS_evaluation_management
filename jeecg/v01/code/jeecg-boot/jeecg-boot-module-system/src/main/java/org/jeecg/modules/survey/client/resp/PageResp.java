package org.jeecg.modules.survey.client.resp;

import java.util.List;


/**
 * @author 一笙
 */
public class PageResp<T> {
    private long total;

    private List<T> records;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> list) {
        this.records = list;
    }

    @Override
    public String toString() {
        return "PageResp{" +
                "total=" + total +
                ", list=" + records +
                '}';
    }
}
