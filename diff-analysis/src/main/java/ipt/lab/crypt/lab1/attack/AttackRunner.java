package ipt.lab.crypt.lab1.attack;

import ipt.lab.crypt.common.utils.PrintUtils;
import ipt.lab.crypt.lab1.datastructures.DiffPairProb;

public class AttackRunner {

    public static void main(String[] args) {
        HeysAttacker attacker = new HeysAttacker();

        DiffPairProb differential = new DiffPairProb(0xe000, 0x2220, 0.001099);
        //new DiffPairProb(0xd000, 0x2220, 0.001099);

        int restoredKey = attacker.attackAttempt(11, differential);
        System.out.println("Restored key = " + PrintUtils.toHexAsShort(restoredKey));
    }
}
