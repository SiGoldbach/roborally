package interfaceLektion1;
interface IStak<E> {
    void push(E x);
    E pop() throws StakEmptyException;
    boolean empty();
    boolean full();
    void show();

    class StakEmptyException extends Exception {
        private static final long serialVersionUID = -7977765377065619635L;

        public StakEmptyException(String msg) {
            super(msg);
        }
    }
}





