package ipt.lab.crypt.lab1.attack;

import ipt.lab.crypt.lab1.attack.HeysAttacker;
import ipt.lab.crypt.lab1.datastructures.DiffPairProb;
import ipt.lab.crypt.common.utils.PrintUtils;

public class AttackRunner {

    public static void main(String[] args) {
        HeysAttacker attacker = new HeysAttacker();

        //attacker.attackAttempt(11, new DiffPairProb(0xd000, 0x2220, 0.001099));
        int restoredKey = attacker.attackAttempt(11, new DiffPairProb(0xe000, 0x2220, 0.001099));
        System.out.println(PrintUtils.toHexAsShort(restoredKey));
    }
}
