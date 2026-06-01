package petadoption;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TablePrinter {
    public static void print(ResultSet rs) throws SQLException {
        ResultSetMetaData meta=rs.getMetaData();
        int columnCount=meta.getColumnCount();
        List<String[]> rows=new ArrayList<>();
        int[] widths=new int[columnCount];

        //store column names
        for (int i = 1; i <= columnCount; i++) {
            widths[i-1]=meta.getColumnLabel(i).length();
        }

        //read all rows and determine widths
        while (rs.next()) {
            String[] row=new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                String value=rs.getString(i);
                if (value==null) {
                    value="NULL";
                }
                row[i-1]=value;
                widths[i-1]=Math.max(widths[i-1],value.length());
            }
            rows.add(row);
        }
        printBorder(widths);

        //header row
        System.out.print("|");
        for (int i = 1; i <= columnCount; i++) {
            String header = meta.getColumnLabel(i);
            System.out.printf(" %-"+widths[i-1]+"s |", header);
        }
        System.out.println();
        printBorder(widths);

        //data rows
        for (String[] row : rows) {
            System.out.print("|");
            for (int i = 0; i < columnCount; i++) {
                System.out.printf(" %-"+widths[i]+"s |", row[i]);
            }
            System.out.println();
        }
        printBorder(widths);
        System.out.println(rows.size()+" row(s) returned.");
    }

    private static void printBorder(int[] widths) {
        System.out.print("+");
        for (int width : widths) {
            for (int i = 0; i < width + 2; i++) {
                System.out.print("-");
            }
            System.out.print("+");
        }
        System.out.println();
    }
}