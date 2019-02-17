package Component.File;

import Component.tool.Tools;
import Component.unit.*;

import java.io.*;
import java.util.ArrayList;
/**
 * Created by snowf on 2019/2/17.
 *
 */
public class FastqFile extends AbstractFile<FastqItem> {

    public FastqFile(File file) {
        super(file);
    }

    public FastqFile(String s) {
        this(new File(s));
    }

    public FastqFile(FastqFile file) {
        super(file);
    }

    @Override
    protected FastqItem ExtractItem(String s) {
        if (s == null) {
            Item = null;
        } else {
            String[] ss = s.split("\\n+");
            Item = new FastqItem();
            Item.Title = ss[0];
            Item.Sequence = ss[1];
            Item.Orientation = ss[2];
            Item.Quality = ss[3];
        }
        return Item;
    }


    @Override
    public synchronized String ReadItemLine() throws IOException {
        StringBuilder s = new StringBuilder();
        for (int i = 1; i <= 4; i++) {
            String line = reader.readLine();
            if (line != null) {
                s.append(line).append("\n");
            } else {
                return null;
            }
        }
        s.deleteCharAt(s.length() - 1);
        return s.toString();
    }

    @Override
    public FastqItem ReadItem() throws IOException {
        Item = new FastqItem();
        Item.Title = reader.readLine();
        Item.Sequence = reader.readLine();
        Item.Orientation = reader.readLine();
        Item.Quality = reader.readLine();
        if (Item.Quality == null)
            Item = null;
        return Item;
    }

    @Override
    public void WriteItem(FastqItem item) throws IOException {
        writer.write(item.toString());
    }

    @Override
    public SortItem ReadSortItem() {
        return null;
    }


    public Opts.FileFormat FastqPhred() throws IOException {
        ReadOpen();
        int[] FormatEdge = new int[]{(int) '9', (int) 'K'};
        int[] Count = new int[2];
        int LineNum = 0;
        while ((Item = ReadItem()) != null && ++LineNum <= 100) {
            for (int i = 0; i < Item.Quality.length(); i++) {
                if ((int) Item.Quality.charAt(i) <= FormatEdge[0]) {
                    Count[0]++;
                } else if ((int) Item.Quality.charAt(i) >= FormatEdge[1]) {
                    Count[1]++;
                }
            }
        }
        return Count[0] >= Count[1] ? Opts.FileFormat.Phred33 : Opts.FileFormat.Phred64;
    }

    public String AdapterDetect(File Prefix, int SubIndex) throws IOException, InterruptedException {
        StringBuilder Adapter = new StringBuilder();
        ArrayList<char[]> MsaStat = new ArrayList<>();
        int SeqNum = 31;
        int[] CountArrays = new int[255];
        FastaFile HeadFile = new FastaFile(Prefix + ".head" + SeqNum);
        FastaFile MsaFile = new FastaFile(Prefix + ".msa");
        ReadOpen();
        BufferedWriter writer = new BufferedWriter(new FileWriter(HeadFile));
        FastqItem fastq_item;
        int linenumber = 0;
        while ((fastq_item = ReadItem()) != null && ++linenumber <= SeqNum) {
            writer.write(fastq_item.Title.replace("@", ">") + "\n");
            writer.write(fastq_item.Sequence.substring(SubIndex) + "\n");
        }
        ReadClose();
        writer.close();
        String ComLine = "mafft " + HeadFile.getPath();
        Opts.CommandOutFile.Append(ComLine + "\n");
        PrintWriter msa = new PrintWriter(MsaFile);
        if (Configure.DeBugLevel < 1) {
            Tools.ExecuteCommandStr(ComLine, msa, null);
        } else {
            Tools.ExecuteCommandStr(ComLine, msa, new PrintWriter(System.err));
        }
        msa.close();
        HeadFile.delete();
//        BufferedReader reader = new BufferedReader(new FileReader(MsaFile));
        MsaFile.ReadOpen();
//        reader.readLine();
        FastaItem fasta_item;
        while ((fasta_item = MsaFile.ReadItem()) != null) {
//            if (fasta_item.Title.matches("^>.*")) {
//                MsaStat.add(Adapter.toString().toCharArray());
//                Adapter.setLength(0);
//                continue;
//            }
//            Adapter.append(fasta_item.Sequence);
            MsaStat.add(fasta_item.Sequence.toString().toCharArray());
        }
//        Adapter.setLength(0);
        MsaFile.ReadClose();
        for (int i = 0; i < MsaStat.get(0).length; i++) {
            CountArrays['A'] = 0;
            CountArrays['T'] = 0;
            CountArrays['C'] = 0;
            CountArrays['G'] = 0;
            CountArrays['-'] = 0;
            for (int j = 0; j < MsaStat.size(); j++) {
                CountArrays[Character.toUpperCase(MsaStat.get(j)[i])]++;
            }
            int MaxValue = 0;
            char MaxBase = '-';
            for (char base : new char[]{'A', 'T', 'C', 'G', '-'}) {
                if (CountArrays[base] > MaxValue) {
                    MaxValue = CountArrays[base];
                    MaxBase = base;
                }
            }
            if (MaxValue > SeqNum / 2) {
                Adapter.append(MaxBase);
            } else {
                Adapter.append('N');
            }
        }
        String[] SplitAdapter = Adapter.toString().replace("-", "").split("N+");
        int MaxValue = 0;
        for (int i = 0; i < SplitAdapter.length; i++) {
            if (SplitAdapter[i].length() > MaxValue) {
                MaxValue = SplitAdapter[i].length();
                Adapter = new StringBuilder(SplitAdapter[i]);
            }
        }
        return Adapter.toString();
    }


}
