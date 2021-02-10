package io.kurau.example;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class Freemarker {

    private Map<String, Object> dataModel = new HashMap<>();
    private File tempDirectory;

    public Freemarker tmpDir(File tmp) {
        tempDirectory = tmp;
        return this;
    }

    public Freemarker onData(Map<String, Object> newModel) {
        dataModel = newModel;
        return this;
    }
    public Freemarker addParam(String key, Object value) {
        dataModel.put(key, value);
        return this;
    }

    public File create(String name) throws IOException {
        Template temp = configuration().getTemplate("template.ftlh");
        File output = new File(tempDirectory, name);
        System.out.println(output.getAbsolutePath());
        try {
            Writer out = new FileWriter(output);
            temp.process(dataModel, out);
            return output;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Can't process freemarker template");
        }
    }


    public Freemarker print() throws IOException {
        Template temp = configuration().getTemplate("template.ftlh");
        try {
            Writer text = new StringWriter();
            temp.process(dataModel, text);
            System.out.println(text.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public Configuration configuration() {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
        cfg.setTemplateLoader(new ClassTemplateLoader(Freemarker.class.getClassLoader(), "templates"));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        return cfg;
    }
}
