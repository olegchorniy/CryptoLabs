package ipt.lab.crypt.lab1.attack;

import ipt.lab.crypt.common.utils.PrintUtils;
import ipt.lab.crypt.lab1.datastructures.DiffPairProb;

public class AttackRunner {

    public static void main(String[] args) {
        HeysAttacker attacker = new HeysAttacker();

        DiffPairProb differential = new DiffPairProb(0x8000, 0x2222, 0.00068624);

        int restoredKey = attacker.attackAttempt(11, differential);
        System.out.println("Restored key = " + PrintUtils.toHexAsShort(restoredKey));
    }
}
