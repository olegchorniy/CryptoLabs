package ipt.lab.crypt.lab2;

import ipt.lab.crypt.common.Constants;
import ipt.lab.crypt.common.utils.PrintUtils;

public class LinearAttackRunner {

    public static void main(String[] args) {
        /*
      + a = 00c0, b = 1011, LP = 0,00032777
      + a = 0020, b = 0110, LP = 0,00037890
      - a = 0010, b = 0110, LP = 0,00040228

        a = 00c0, b = 1111, LP = 0,00016018
        a = 00e0, b = 1111, LP = 0,00016155
        a = 0090, b = 1111, LP = 0,00013384
      + a = 00d0, b = 1111, LP = 0,00015632

      - a = 000c, b = 2222, LP = 0,00013905
     -- a = 0c0c, b = 4264, LP = 0,00003
         */
        int a = 0x0020;
        int b = 0x0110;

        LinearAttacker attacker = new LinearAttacker(Constants.VARIANT);
        int key = attacker.attackKey(a, b);

        System.out.println("key = " + PrintUtils.toHexAsShort(key));
    }
}
