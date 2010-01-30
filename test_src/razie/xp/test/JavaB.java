package razie.xp.test;

public class JavaB extends JavaA {
   
   JavaA a = new JavaA("a");
   JavaA b = new JavaA("b");
   
   public JavaA getA () { return a; }
  
   public JavaB (String value) { super(value); }
   
}