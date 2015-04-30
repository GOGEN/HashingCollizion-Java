package helpers;

/**
 * Created by gogen on 29.04.15.
 */
public class Tuple<T, K> {
    public T first;
    public K second;

    public Tuple(T t, K k){
        first = t;
        second = k;
    }
}
