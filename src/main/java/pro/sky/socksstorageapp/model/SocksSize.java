package pro.sky.socksstorageapp.model;

public enum SocksSize {

    XS(35, 37),
    S(38, 40),
    M(41, 43),
    L(44, 46);

    final Integer minSize;

    final Integer maxSize;

    SocksSize(Integer minSize, Integer maxSize) {
        this.minSize = minSize;
        this.maxSize = maxSize;
    }

    public Integer getMinSize() {
        return minSize;
    }

    public Integer getMaxSize() {
        return maxSize;
    }
}
