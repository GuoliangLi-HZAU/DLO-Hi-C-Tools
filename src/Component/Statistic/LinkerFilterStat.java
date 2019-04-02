package Component.Statistic;

import Component.File.AbstractFile;
import Component.File.CommonFile;
import Component.unit.Configure;
import Component.unit.LinkerSequence;
import Component.unit.StringArrays;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * Created by snowf on 2019/3/1.
 */

public class LinkerFilterStat extends AbstractStat {
    public LinkerSequence[] Linkers = new LinkerSequence[0];
    public String[] HalfLinkers;
    public String[] Adapters;
    public int Threshold;
    public String EnzymeCuttingSite = "";
    public File OutDir;

    //--------------------------------------------------------------------
    public CommonFile InputFile;
    public long[] LeftValidPairNum = new long[0], RightValidPairNum = new long[0];
    public long[] ValidPairNum = new long[0];
    public long[] AddBaseToLeftPair = new long[0], AddBaseToRightPair = new long[0];
    public long[] LinkerMatchableNum = new long[0];
    public HashMap<String, int[]> LinkerMatchScoreDistribution = new HashMap<>();
    public HashMap<Integer, int[]>[] ReadsLengthDistributionR1 = new HashMap[0];
    public HashMap<Integer, int[]>[] ReadsLengthDistributionR2 = new HashMap[0];

    public long AllLinkerMatchable;
    public long LinkerUnmatchableNum;
    public long AdapterMatchableNum;
    public long AdapterUnmatchableNum;
    public long AllValidLeftPair;
    public long AllLeftAddBase;
    public long AllValidRightPair;
    public long AllRightAddBase;
    public long AllValidPair;


    public int ThreadNum;

    public LinkerFilterStat() {
    }

    @Override
    public void Stat() {

    }

    @Override
    public String Show() {
        //show.append( ).append("\t").append(new DecimalFormat("#,###").format( )).append("\t").append(String.format("%.2f", )).append("%").append("\n");
        UpDate();
        StringBuilder show = new StringBuilder();
        show.append("##================================Linker filter statistic=====================================\n");
        show.append("Enzyme cutting site:\t").append(EnzymeCuttingSite).append("\n");
        show.append("Half-Linkers:\t").append(String.join(" ", HalfLinkers)).append("\n");
        show.append("Adapters:\t").append(String.join(" ", Adapters)).append("\n");
        show.append("Match score: ").append(Configure.MatchScore).append("\tMismatch score: ").append(Configure.MisMatchScore).append("\tInsert & Delete score: ").append(Configure.InDelScore).append("\n");
        show.append("Linker mapping minimum quality:\t").append(Threshold).append("\n");
        show.append("Output directory:\t").append(OutDir).append("\n");
        show.append("##--------------------------------------------------------------------------------------------\n");
        show.append("Input:").append("\t").append(new DecimalFormat("#,###").format(InputFile.getItemNum())).append("\t").append("-").append("\n");
        show.append("\n");
        for (int i = 0; i < Linkers.length; i++) {
            show.append("Linker ").append(Linkers[i].getType()).append(":\t").append(Linkers[i].getSeq()).append("\n");
            show.append("Linker matchable").append("\t").append(new DecimalFormat("#,###").format(LinkerMatchableNum[i])).append("\t").append(String.format("%.2f", (double) LinkerMatchableNum[i] / InputFile.getItemNum() * 100)).append("%").append("\n");
            show.append("Left pair valid ").append("\t").append(new DecimalFormat("#,###").format(LeftValidPairNum[i])).append("\t").append(String.format("%.2f", (double) LeftValidPairNum[i] / InputFile.getItemNum() * 100)).append("%").append("\t");
            show.append("Add base to left pair").append("\t").append(new DecimalFormat("#,###").format(AddBaseToLeftPair[i])).append("\t").append(String.format("%.2f", (double) AddBaseToLeftPair[i] / InputFile.getItemNum() * 100)).append("%").append("\n");
            show.append("Right pair valid").append("\t").append(new DecimalFormat("#,###").format(RightValidPairNum[i])).append("\t").append(String.format("%.2f", (double) RightValidPairNum[i] / InputFile.getItemNum() * 100)).append("%").append("\t");
            show.append("Add base to right pair").append("\t").append(new DecimalFormat("#,###").format(AddBaseToRightPair[i])).append("\t").append(String.format("%.2f", (double) AddBaseToRightPair[i] / InputFile.getItemNum() * 100)).append("%").append("\n");
            show.append("Valid reads pair").append("\t").append(new DecimalFormat("#,###").format(ValidPairNum[i])).append("\t").append(String.format("%.2f", (double) ValidPairNum[i] / InputFile.getItemNum() * 100)).append("%").append("\n");
            show.append("\n");
        }
        show.append("Total:\n");
        show.append("Linker matchable").append("\t").append(new DecimalFormat("#,###").format(AllLinkerMatchable)).append("\t").append(String.format("%.2f", (double) AllLinkerMatchable / InputFile.getItemNum() * 100)).append("%").append("\t");
        show.append("Linker unmatchable").append("\t").append(new DecimalFormat("#,###").format(LinkerUnmatchableNum)).append("\t").append(String.format("%.2f", (double) LinkerUnmatchableNum / InputFile.getItemNum() * 100)).append("%").append("\n");
        show.append("Adapter matchable").append("\t").append(new DecimalFormat("#,###").format(AdapterMatchableNum)).append("\t").append(String.format("%.2f", (double) AdapterMatchableNum / InputFile.getItemNum() * 100)).append("%").append("\t");
        show.append("Adapter unmatchable").append("\t").append(new DecimalFormat("#,###").format(AdapterUnmatchableNum)).append("\t").append(String.format("%.2f", (double) AdapterUnmatchableNum / InputFile.getItemNum() * 100)).append("%").append("\n");
        show.append("Left pair valid ").append("\t").append(new DecimalFormat("#,###").format(AllValidLeftPair)).append("\t").append(String.format("%.2f", (double) AllValidLeftPair / InputFile.getItemNum() * 100)).append("%").append("\t");
        show.append("Add base to left pair").append("\t").append(new DecimalFormat("#,###").format(AllLeftAddBase)).append("\t").append(String.format("%.2f", (double) AllLeftAddBase / InputFile.getItemNum() * 100)).append("%").append("\n");
        show.append("Right pair valid").append("\t").append(new DecimalFormat("#,###").format(AllValidRightPair)).append("\t").append(String.format("%.2f", (double) AllValidRightPair / InputFile.getItemNum() * 100)).append("%").append("\t");
        show.append("Add base to right pair").append("\t").append(new DecimalFormat("#,###").format(AllRightAddBase)).append("\t").append(String.format("%.2f", (double) AllRightAddBase / InputFile.getItemNum() * 100)).append("%").append("\n");
        show.append("Valid reads pair").append("\t").append(new DecimalFormat("#,###").format(AllValidPair)).append("\t").append(String.format("%.2f", (double) AllValidPair / InputFile.getItemNum() * 100)).append("%").append("\n");
        return show.toString();
    }

    @Override
    protected void UpDate() {
        AllLinkerMatchable = StatUtil.sum(LinkerMatchableNum);
        if (LinkerUnmatchableNum == 0 && AllLinkerMatchable != 0) {
            LinkerUnmatchableNum = InputFile.getItemNum() - AllLinkerMatchable;
        }
        if (AdapterUnmatchableNum == 0 && AdapterMatchableNum != 0) {
            AdapterUnmatchableNum = InputFile.getItemNum() - AdapterMatchableNum;
        }
        AllValidPair = StatUtil.sum(ValidPairNum);
        AllValidLeftPair = StatUtil.sum(LeftValidPairNum);
        AllValidRightPair = StatUtil.sum(RightValidPairNum);
        AllLeftAddBase = StatUtil.sum(AddBaseToLeftPair);
        AllRightAddBase = StatUtil.sum(AddBaseToRightPair);
    }

    @Override
    public void Init() {
        LinkerMatchableNum = new long[Linkers.length];
        ValidPairNum = new long[Linkers.length];
        LeftValidPairNum = new long[Linkers.length];
        RightValidPairNum = new long[Linkers.length];
        AddBaseToLeftPair = new long[Linkers.length];
        AddBaseToRightPair = new long[Linkers.length];
        ReadsLengthDistributionR1 = new HashMap[Linkers.length];
        ReadsLengthDistributionR2 = new HashMap[Linkers.length];
        for (int i = 0; i < Linkers.length; i++) {
            ReadsLengthDistributionR1[i] = new HashMap<>();
            ReadsLengthDistributionR2[i] = new HashMap<>();
        }
    }

    public void WriteLinkerScoreDis(AbstractFile f) throws IOException {
        BufferedWriter outfile = f.WriteOpen();
        outfile.write("Score\tCount\n");
        int[] Keys = StringArrays.toInteger(LinkerMatchScoreDistribution.keySet().toArray(new String[0]));
        int max = StatUtil.max(Keys);
        int min = StatUtil.min(Keys);
        for (int i = min; i <= max; i++) {
            outfile.write(i + "\t");
            if (!LinkerMatchScoreDistribution.containsKey(String.valueOf(i))) {
                outfile.write(0 + "\n");
            } else {
                outfile.write(LinkerMatchScoreDistribution.get(String.valueOf(i))[0] + "\n");
            }
        }
        f.ItemNum = max - min + 1;
        outfile.close();
    }

    public void WriteReadsLengthDis(AbstractFile[] f) throws IOException {
        for (int i = 0; i < f.length; i++) {
            BufferedWriter outfile = f[i].WriteOpen();
            outfile.write("Length\tR1\tR2\n");
            int[] R1Keys = new int[ReadsLengthDistributionR1[i].size()];
            int[] R2Keys = new int[ReadsLengthDistributionR2[i].size()];
            try {
                int index = 0;
                for (Integer k : ReadsLengthDistributionR1[i].keySet()) {
                    R1Keys[index] = k;
                    index++;
                }
                index = 0;
                for (Integer k : ReadsLengthDistributionR2[i].keySet()) {
                    R2Keys[index] = k;
                    index++;
                }
                int max = Math.max(StatUtil.max(R1Keys), StatUtil.max(R2Keys));
                int min = Math.min(StatUtil.min(R1Keys), StatUtil.min(R2Keys));
                for (int j = min; j <= max; j++) {
                    outfile.write(j + "\t");
                    if (!ReadsLengthDistributionR1[i].containsKey(j)) {
                        outfile.write(0 + "\t");
                    } else {
                        outfile.write(ReadsLengthDistributionR1[i].get(j)[0] + "\t");
                    }
                    if (!ReadsLengthDistributionR2[i].containsKey(j)) {
                        outfile.write(0 + "\n");
                    } else {
                        outfile.write(ReadsLengthDistributionR2[i].get(j)[0] + "\n");
                    }
                }
                outfile.close();
            } catch (IndexOutOfBoundsException e) {
                outfile.close();
            }
        }
    }

}
