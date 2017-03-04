package ipt.lab.crypt.lab1;

import ipt.lab.crypt.lab1.core.HeysAttacker;
import ipt.lab.crypt.lab1.datastructures.DiffPairProb;
import ipt.lab.crypt.lab1.utils.PrintUtils;

public class AttackRunner {

    public static void main(String[] args) {
        HeysAttacker attacker = new HeysAttacker();

        System.out.println(PrintUtils.toHexAsShort(attacker.attackAttempt(11, new DiffPairProb(0xd000, 0x2220, 0.001099))));
        System.out.println(PrintUtils.toHexAsShort(attacker.attackAttempt(11, new DiffPairProb(0xe000, 0x2220, 0.001092))));
    }
}
