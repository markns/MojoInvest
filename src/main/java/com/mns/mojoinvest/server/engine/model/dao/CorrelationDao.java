package com.mns.mojoinvest.server.engine.model.dao;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.math.linear.BlockRealMatrix;
import org.apache.commons.math.linear.RealMatrix;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CorrelationDao {

    //    private static double[][] data =
    List<String> symbols = Arrays.asList("IH2O", "ITKY", "SE15", "IUKP", "SEUA", "SEGA", "IKOR", "IEUX", "IUSE", "IEUR", "IEUT", "IUSA", "IFFF", "IEMS", "IUKD", "IEER", "IEEM", "LQDE", "IBGL", "IBGM", "IMIB", "IBTM", "DJMC", "IWDP", "IAEX", "SPAG", "IEGE", "IBTS", "BRIC", "IXMU", "IWDE", "IPRP", "IPRV", "NFTY", "EEX5", "SWDA", "ISFE", "INRG", "EUE", "IBZL", "IGSG", "RUSS", "EUN", "SPOG", "SPOL", "IBGS", "ISXF", "IGCC", "IGIL", "IBGX", "IEMI", "SACC", "ISEM", "ISP6", "IHYG", "INAA", "SUAG", "IAPD", "ISWD", "INXG", "IDJG", "IGLS", "IGLT", "IBCI", "IEAG", "IGLO", "SHYU", "FXC", "SSAM", "WOOD", "SLXX", "IGWD", "IJPE", "ICOV", "SPGP", "IWXU", "SCAN", "IJPN", "ISF", "IDJV", "SMEA", "ISUS", "EEXF", "SEDY", "INFR", "ITPS", "SAUS", "LTAM", "IEGZ", "IEGY", "ISJP", "SEMA", "SEMB", "IESE", "SJPA", "IS15", "SRSA", "IWRD", "IBCX", "IMEU", "IEAC", "IPXJ", "IUSP", "DJSC", "IGUS", "MIDD", "IDVY", "IASP", "ITWN");

    RealMatrix m;


    public double getCorrelation(String a, String b) {
        if (m == null) {
            try {

                String text = IOUtils.toString(CorrelationDao.class.getClassLoader()
                        .getResourceAsStream("ishares_correl.xml"), "UTF-8");

//                String text = Files.toString(new File("/Users/marknuttallsmith/Projects/MojoInvest/data/ishares_correl.xml"), Charsets.UTF_8);
                XStream xStream = new XStream();
                m = (BlockRealMatrix) xStream.fromXML(text);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int x = symbols.indexOf(a);
        int y = symbols.indexOf(b);
        return m.getEntry(x, y);
    }
}
