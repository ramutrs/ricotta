package supportFunctions;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class reusableMethods {

 /*
  * *************************************************************************
  * Method Name 	: searchPolicy
  * Description 	: This function will search a policy number in a huge PDF
  *                   and split the huge PDF into multiple small PDFs based on
  *                   start & end page logic
  * Input Parameter  : policyNo, filename, fromfile, tofile
  * Output Parameter : none
  * Author 			: Ramasamy
  * *************************************************************************
  */
    public void searchPolicy(String policyNo, String filename, File fromfile, File tofile) throws IOException {
        String pdfFileName = filename.toUpperCase();
        String fileName = fromfile + "\\"+pdfFileName + ".pdf";
        PDDocument pddoc = PDDocument.load(new File(fileName));
        int toPage =0 ,frompage=0,lastPage = 0;
        String content = "";
        PDFTextStripper splitdoc = new PDFTextStripper();
        int totalNoOfPages = pddoc.getNumberOfPages();
        System.out.println("Total no of pages :" + totalNoOfPages);
//      Looping through all pages & finding the from & topage for splitting
        for (int pageno = 1; pageno <= totalNoOfPages; pageno++) {
            splitdoc.setStartPage(pageno);
            splitdoc.setEndPage(pageno);
            content = splitdoc.getText(pddoc);
//      Finding From Page
            if (content.contains(policyNo)) {
                if (frompage == 0) {
                    frompage = pageno;
                    System.out.println("From Page is : " + frompage);
                }
            }
//      Finding to page
            else if (frompage != 0 && toPage == 0) {
                toPage = pageno;
//          When policy in last page of the PDF
                if (pageno == totalNoOfPages) {
                    lastPage = toPage;
                    System.out.println("To Page Found " + lastPage);
                }
//          Finding last page
                else {
                    splitdoc.setStartPage(toPage);
                    splitdoc.setEndPage(toPage);
                    String topagecontent = splitdoc.getText(pddoc);
                    if (topagecontent.contains("Dear")) {
                        lastPage = toPage - 1;
                        System.out.println("To Page is " + toPage);
                    } else {
                        toPage = 0;
                    }
                }
            }
        }
        // Splitting the PDF from a start & end page
        Splitter splitter = new Splitter();
        splitter.setStartPage(frompage);
        splitter.setEndPage(lastPage);
        splitter.setSplitAtPage(lastPage - frompage + 1);
        List<PDDocument> lst = splitter.split(pddoc);

        //Convert the splitted partial doc into a PDF
        PDDocument pdfDocPartial = lst.get(0);
        File f = new File(tofile + policyNo + ".pdf");
        FileWriter fw = new FileWriter(f);

        pdfDocPartial.save(f);
        System.out.println("Multiple PDF files are created successfully.");

        pddoc.close();
    }

    public static void main(String args[]) throws IOException {
        reusableMethods sc = new reusableMethods();
        System.out.println(args[0]);
        String[] arg=args[0].split("#");
        File file = new File(arg[0]);
        System.out.println(arg[0]);
        FileInputStream fis = new FileInputStream(file);
        Properties prop = new Properties();
        prop.load(fis);
        String policyNo = prop.getProperty("multiplePolicyNo");
        String testArray[] = policyNo.split(",");
        String fileName = prop.getProperty("searchFileName");
        File fromfile = new File(arg[1]);
        System.out.println(arg[1]);
        File tofile = new File(arg[2]);
        System.out.println(arg[2]);
        for (String keyWord : testArray) {
            sc.searchPolicy(keyWord, fileName,fromfile,tofile);
        }
    }

    /*
     * *************************************************************************
     * Method Name 		: Generate_RUNID
     * Description 		: This function will generate RUn_ID based on the current
     * 						execution session
     * Input Parameter  :
     * Output Parameter : String
     * Author 			:
     * *************************************************************************
     */
    public static String Generate_RUNID() {
        // getting current date and time using Date class
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateobj = new Date();

        // Declare required variables to update Run Log
        String gen_strRunID;

        // Assign the details to be inserted to Run Log
        String strTimeStamp = df.format(dateobj);
        gen_strRunID = strTimeStamp.replace("/", "");
        gen_strRunID = gen_strRunID.replace(":", "");
        gen_strRunID = gen_strRunID.replace(" ", "_");
        return gen_strRunID;
    }


}
