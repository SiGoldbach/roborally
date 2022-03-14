
package interfaceLektion1;

class StakElement<D> {
    StakElement next;
    D data;

    StakElement(D s, StakElement e) {
        data = s;
        next = e;
    }
}

public class LinkedStak<E> implements IStak<E> {

    private StakElement<E> start;

    public LinkedStak() {
        start = null;
    }

    public void push(E s) {
        StakElement temp;
        temp = new StakElement(s, start);
        start = temp;
    }

    public E pop() {
        if (empty()) return null;
        E s = start.data;
        start = start.next;
        return s;
    }

    public boolean empty() {
        return start == null;
    }

    public boolean full() {
        return false;
    }

    public void show() {
        for (StakElement p = start; p != null; p = p.next)
            System.out.println(p.data + " ");
        System.out.println("*");
    }
}

