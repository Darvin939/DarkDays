package darvin939.DarkDays.Utils;

import java.util.Random;

public class Rnd {

   private static final Integer a = Integer.valueOf(2);
   private static final Integer b = Integer.valueOf(3);


   public static boolean get(Integer percent) {
      if(isPercent(percent)) {
         Integer[] i = getPeriod(percent);
         int r = (new Random()).nextInt(i[1].intValue()) + i[0].intValue();
         if(percent.intValue() >= r) {
            return true;
         }
      }

      return false;
   }

   private static boolean isPercent(Integer chance) {
      int type = getType(chance).intValue();
      Random r = new Random();
      int summ = 0;

      for(int i = 0; i < type; ++i) {
         int s = r.nextInt(b.intValue());
         if(s < a.intValue()) {
            ++summ;
         }
      }

      if(summ == type) {
         return true;
      } else {
         return false;
      }
   }

   private static Integer getType(Integer chance) {
      byte type;
      if(chance.intValue() > 84) {
         type = 1;
      } else if(chance.intValue() <= 84 && chance.intValue() > 68) {
         type = 2;
      } else if(chance.intValue() <= 68 && chance.intValue() > 52) {
         type = 3;
      } else if(chance.intValue() <= 52 && chance.intValue() > 36) {
         type = 4;
      } else if(chance.intValue() <= 36 && chance.intValue() > 20) {
         type = 5;
      } else if(chance.intValue() <= 20 && chance.intValue() > 4) {
         type = 6;
      } else {
         type = 7;
      }

      return Integer.valueOf(type);
   }

   private static Integer[] getPeriod(Integer chance) {
      int type = getType(chance).intValue();
      Integer[] out = new Integer[]{Integer.valueOf(0), Integer.valueOf(100)};
      if(type == 1) {
         out = new Integer[]{Integer.valueOf(84), Integer.valueOf(16)};
      }

      if(type == 2) {
         out = new Integer[]{Integer.valueOf(68), Integer.valueOf(16)};
      }

      if(type == 3) {
         out = new Integer[]{Integer.valueOf(52), Integer.valueOf(16)};
      }

      if(type == 4) {
         out = new Integer[]{Integer.valueOf(36), Integer.valueOf(16)};
      }

      if(type == 5) {
         out = new Integer[]{Integer.valueOf(20), Integer.valueOf(16)};
      }

      if(type == 6) {
         out = new Integer[]{Integer.valueOf(4), Integer.valueOf(16)};
      }

      if(type == 7) {
         out = new Integer[]{Integer.valueOf(0), Integer.valueOf(4)};
      }

      return out;
   }
}
