package top.easelink.lcg;

import java.util.ArrayList;
import java.util.List;

public class Test {


    public class A {

    }

    public class B extends A{

    }

    public static void fill(List<? super A> dest, List<? extends A> test) {

    }

    public void test() {
        List<Object> a = new ArrayList<Object>();
        a.add(new Object());

        List<B> b = new ArrayList<>();
        b.add(new B());

        fill(a, b);
    }

}
