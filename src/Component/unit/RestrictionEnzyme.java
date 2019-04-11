package Component.unit;

/**
 * Created by snowf on 2019/4/11.
 */

public class RestrictionEnzyme {

    public static final RestrictionEnzyme HindIII = new RestrictionEnzyme("HindIII", "A^AGCTT");
    public static final RestrictionEnzyme MseI = new RestrictionEnzyme("MseI", "A^ATT");
    public static final RestrictionEnzyme ClaI = new RestrictionEnzyme("ClaI", "AT^CGAT");
    public static final RestrictionEnzyme[] list = new RestrictionEnzyme[]{HindIII, MseI, ClaI};
    private static final String Delimiter = "^";

    private String Name;
    private String Sequence;
    private int CutSite;

    public RestrictionEnzyme(String name, String s) {
        Name = name;
        if (s.matches(".*[^a-z|A-Z].*")) {
            s = s.replaceAll("[^a-z|A-Z]+", Delimiter);
            CutSite = s.indexOf(Delimiter);
            Sequence = s.replaceAll("\\" + Delimiter, "");
        } else {
            Sequence = s;
            CutSite = 0;
        }
    }

    public static void main(String[] args) {
        RestrictionEnzyme test1 = new RestrictionEnzyme("AA*GCTT");
        RestrictionEnzyme test2 = new RestrictionEnzyme("AA__GCTT");
    }

    public RestrictionEnzyme(String s) {
        boolean flag = false;
        for (int i = 0; i < list.length; i++) {
            if (s.compareToIgnoreCase(list[i].getName()) == 0) {
                copy(list[i]);
                flag = true;
                break;
            }
        }
        if (!flag) {
            copy(new RestrictionEnzyme("", s));
        }
    }

    public void copy(RestrictionEnzyme r) {
        Name = r.Name;
        Sequence = r.Sequence;
        CutSite = r.CutSite;
    }

    public String getName() {
        return Name;
    }

    public String getSequence() {
        return Sequence;
    }

    public int getCutSite() {
        return CutSite;
    }

    @Override
    public String toString() {
        return Sequence.substring(0, CutSite) + Delimiter + Sequence.substring(CutSite);
    }
}