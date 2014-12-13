package it.eng.eli4u.service.velocity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResource;


public class StringResourceLoader extends ResourceLoader {

    protected static int classMetricsLevel;
    protected static AtomicLong totalClassInstancesCreatedCount;
    protected static AtomicLong totalStringsGeneratedCount;

    static {
        classMetricsLevel = 1;
        totalClassInstancesCreatedCount = new AtomicLong();
        totalStringsGeneratedCount = new AtomicLong();
    }


    protected String encoding;


    public StringResourceLoader() {
        if ( classMetricsLevel > 0 ) {
            totalClassInstancesCreatedCount.incrementAndGet();
        }
        this.encoding = "UTF-8";
    }

    @Override
    public void init(ExtendedProperties paramExtendedProperties) {
        String paramEncoding = paramExtendedProperties.getString("encoding");
        
        if (paramEncoding != null && paramEncoding.trim().length() > 0) {
            this.encoding = paramEncoding;
        }
    }

    @Override
    public InputStream getResourceStream(String contents) {
        if (classMetricsLevel > 1) {
            totalStringsGeneratedCount.incrementAndGet();
        }

        StringResource resource = new StringResource(contents, encoding);

        byte[] byteArray = null;
        try {
            byteArray = resource.getBody().getBytes(resource.getEncoding());
            return new ByteArrayInputStream(byteArray);
        } catch (UnsupportedEncodingException ue) {
            throw new VelocityException("Failed to convert contents to String " + resource.getEncoding(), ue);
        }
    }

    @Override
    public boolean isSourceModified(Resource paramResource) {
        return false;
    }

    @Override
    public long getLastModified(Resource paramResource) {
        return 0;
    }


    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public static int getClassMetricsLevel() {
        return classMetricsLevel;
    }

    public static void setClassMetricsLevel(int classMetricsLevel) {
        StringResourceLoader.classMetricsLevel = classMetricsLevel;
    }

    public static void setTotalClassInstancesCreatedCount(long counterValue) {
        StringResourceLoader.totalClassInstancesCreatedCount.getAndSet(counterValue);
    }

    public static long getTotalClassInstancesCreatedCount() {
        return StringResourceLoader.totalClassInstancesCreatedCount.get();
    }

    public static void setTotalStringsGeneratedCount(long counterValue) {
        StringResourceLoader.totalStringsGeneratedCount.getAndSet(counterValue);
    }

    public static long getTotalStringsGeneratedCount() {
        return StringResourceLoader.totalStringsGeneratedCount.get();
    }

    @Override
    public String toString() {
        return "StringResourceLoader [encoding=" + encoding + "]";
    }

}
