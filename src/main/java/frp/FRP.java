package frp;

class FRP {
    public static void main(String[] args) {
        var a = Var.of(10);
        var b = Var.bind(a, x -> x + 1);
//        System.out.println(b.value());

        a.setValue(20);
        System.out.println(b.value());
    }
}
