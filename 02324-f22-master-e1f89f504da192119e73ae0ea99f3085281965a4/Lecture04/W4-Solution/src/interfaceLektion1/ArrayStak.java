package interfaceLektion1;

public class ArrayStak<SE> implements IStak<SE>{

    private SE data[];
    private int staktop;
    private final int antal;

    public ArrayStak(int n){
        antal = n;
        data = (SE[]) new Object[antal];
        staktop = -1;
    }

    public void push(SE v){
        data[++staktop]=v;

    }

    public SE pop() throws StakEmptyException {
        if (staktop == -1)
            throw new StakEmptyException("Der er ingen elementer");
        return data[staktop--];
    }


    public boolean empty(){
        return staktop == -1;
    }

    public boolean full() {
        return (staktop ==(antal -1));
    }

    public void show(){
        for (int i = staktop; i>-1; i--)
            System.out.println(data[i]+" ");
        System.out.println("*");
    }
}