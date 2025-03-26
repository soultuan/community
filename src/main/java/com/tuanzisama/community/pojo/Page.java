package com.tuanzisama.community.pojo;

public class Page {

    //当前页码
    private Integer current = 1;
    //显示上限
    private Integer limit = 10;
    //数据总数
    private Integer rows;
    //查询路径
    private String path;

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        if(current >= 1){
            this.current = current;
        }
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer pageSize) {
        if(pageSize >= 1&&pageSize <= 100){
            this.limit = pageSize;
        }
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer total) {
        if(total >= 0){
            this.rows = total;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getOffset() {
        return (current - 1) * limit;
    }

    public int getTotal(){
        if(rows%limit==0){
            return rows/limit;
        }else{
            return rows/limit+1;
        }
    }

    public int getFrom(){
        return Math.max(current - 2, 1);
    }

    public int getTo(){
        return Math.min(current + 2, getTotal());
    }
}
