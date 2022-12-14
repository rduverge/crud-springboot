package com.app.web.controlador;

import com.app.web.servicio.CarrosServicio;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.poi.ss.usermodel.Row;
import com.app.web.entidad.Carros;
import com.app.web.excel.ExcelExporter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.app.web.excel.PdfExporter;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


import com.lowagie.text.DocumentException;



@Controller
public class CarrosControlador {

    @Autowired
    private CarrosServicio servicio;

        @GetMapping({ "/carros", "/" })
        public String listarCarros(Model modelo) {
            modelo.addAttribute("carros", servicio.listarTodosLosCarros());
            return "carros";
        }

        @GetMapping("/carros/nuevo")
        public String registrarCarro(Model modelo) {
            Carros carro = new Carros();
            modelo.addAttribute("carro", carro);
            return "crear_carro";
        }
        @PostMapping("/carros")
        public String guardarCarro(@ModelAttribute("Carro") Carros Carro) {
            servicio.guardarCarro(Carro);
            return "redirect:/carros/nuevo";
        }
        @GetMapping("/carros/editar/{id}")
        public String mostrarFormularioDeEditar(@PathVariable Long id, Model modelo) {
            modelo.addAttribute("carro", servicio.obtenerCarroId(id));
            return "editar_carro";
        }

        @PostMapping("/carros/{id}")
        public String actualizarCarro(@PathVariable Long id, @ModelAttribute("carro") Carros Carro,Model modelo) {
            Carros carrosExistente = servicio.obtenerCarroId(id);
            carrosExistente.setId(id);
            carrosExistente.setModelo(Carro.getModelo());
            carrosExistente.setMarca(Carro.getMarca());
            carrosExistente.setDescripcion(Carro.getDescripcion());
            carrosExistente.setNumero_de_Chasis(Carro.getNumero_de_Chasis());
            carrosExistente.setNumero_de_Placa(Carro.getNumero_de_Placa());
            carrosExistente.setTipo_Combustible(Carro.getTipo_Combustible());
            carrosExistente.setTipo_de_vehiculo(Carro.getTipo_de_vehiculo());

            servicio.actualizarCarro(carrosExistente);
            return "redirect:/carros";
        }

        @GetMapping("/carros/{id}")
        public String eliminarCarro(@PathVariable Long id) {
            servicio.eliminarCarro(id);
            return "redirect:/carros";
        }

    @GetMapping("/carros/export/excel")
    public void exportToExcel(HttpServletResponse response) throws  IOException{
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter= new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime= dateFormatter.format(new Date());

        String headerKey= "Content-Disposition";
        String headerValue= "attachment; filename=Carros" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<Carros> listCarro= servicio.listarTodosLosCarros();
        ExcelExporter excelExporter= new ExcelExporter(listCarro);
        excelExporter.export(response);
    }

    @GetMapping("/carros/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<Carros> listaCarros = servicio.listarTodosLosCarros();

        PdfExporter exporter = new PdfExporter(listaCarros);
        exporter.export(response);
    }
    @GetMapping("/users/data/import")
    public String DatagettingFromExcel() throws IOException {
        File fileDriver = new File("D:\\Gecko\\chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", fileDriver.getAbsolutePath());
        WebDriver driver =new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.get("http://localhost:8090/carros/nuevo");
        FileInputStream fis=new FileInputStream("C:\\Users\\Rolando\\Desktop\\SpringBoot-Crud-Cars-main\\src\\excel\\CarrosForm.xlsx");
        XSSFWorkbook workbook=new XSSFWorkbook(fis);
        XSSFSheet sheet=workbook.getSheet("sheet");
        int rowCount =sheet.getLastRowNum();
        int colCount=sheet.getRow(1).getLastCellNum();
        System.out.println("rowcount :"+rowCount+" colcount : "+colCount);
        for (int i = 1; i < rowCount; i++) {
            XSSFRow cellData=sheet.getRow(i);

            String marca=cellData.getCell(0).getStringCellValue();
            String modelo=cellData.getCell(1).getStringCellValue();
            String descripcion=cellData.getCell(2).getStringCellValue();
            String tipo_Combustible=cellData.getCell(3).getStringCellValue();
            String numero_de_Chasis=cellData.getCell(4).getStringCellValue();
            String numero_de_Placa=cellData.getCell(5).getStringCellValue();
            String tipo_de_vehiculo=cellData.getCell(6).getStringCellValue();

            driver.findElement(By.cssSelector("#marca")).clear();
            driver.findElement(By.cssSelector("#marca")).sendKeys(marca);
            driver.findElement(By.cssSelector("#modelo")).clear();
            driver.findElement(By.cssSelector("#modelo")).sendKeys(modelo);
            driver.findElement(By.cssSelector("#descripcion")).clear();
            driver.findElement(By.cssSelector("#descripcion")).sendKeys(descripcion);
            driver.findElement(By.cssSelector("#tipo_Combustible")).clear();
            driver.findElement(By.cssSelector("#tipo_Combustible")).sendKeys(tipo_Combustible);
            driver.findElement(By.cssSelector("#numero_de_Chasis")).clear();
            driver.findElement(By.cssSelector("#numero_de_Chasis")).sendKeys(numero_de_Chasis);
            driver.findElement(By.cssSelector("#numero_de_Placa")).clear();
            driver.findElement(By.cssSelector("#numero_de_Placa")).sendKeys(numero_de_Placa);
            driver.findElement(By.cssSelector("#tipo_de_vehiculo")).clear();
            driver.findElement(By.cssSelector("#tipo_de_vehiculo")).sendKeys(tipo_de_vehiculo);
            driver.findElement(By.cssSelector("#submit")).click();

            System.out.println(i+"."+marca+" // " +modelo+" // "+descripcion+" // "+tipo_Combustible+" // "+numero_de_Chasis+" // "+numero_de_Placa+" // "+tipo_de_vehiculo);
        }
        return "redirect:/carros/nuevo";
    }
}