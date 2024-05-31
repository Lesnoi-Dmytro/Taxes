module org.devices.taxes {
	requires javafx.controls;
	requires javafx.fxml;
	requires static lombok;
	requires com.fasterxml.jackson.core;
	requires org.apache.logging.log4j;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.datatype.jsr310;
	requires java.net.http;
	requires org.controlsfx.controls;


	opens org.taxes.app.auth to javafx.fxml, javafx.graphics;
	opens org.taxes.app.user to javafx.fxml, javafx.graphics, com.fasterxml.jackson.databind;
	opens org.taxes.app.company to javafx.fxml, javafx.graphics, com.fasterxml.jackson.databind;
	opens org.taxes.tax to com.fasterxml.jackson.databind;
	opens org.taxes.user to com.fasterxml.jackson.databind;
	opens org.taxes.app.auth.req to com.fasterxml.jackson.databind;
}