package org.jboss.seam.pdf.ui;

import javax.faces.context.FacesContext;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;

public class UIBarChart 
    extends UICategoryChartBase
{
    private CategoryDataset dataset;
    
    private String title;
    private String categoryAxisLabel;
    private String valueAxisLabel;
       
    private String orientation;
    
    private boolean legend;
    private boolean is3D = false;

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getTitle() {
        return (String) valueBinding("title", title);
    }
    
    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }
    
    public String getOrientation() {
        return (String) valueBinding("orientation", orientation);
    }
    
    public void setCategoryAxisLabel(String categoryAxisLabel) {
        this.categoryAxisLabel = categoryAxisLabel;
    }
    
    public String getCategoryAxisLabel() {
        return (String) valueBinding("categoryAxisLabel", categoryAxisLabel);
    }

    public void setValueAxisLabel(String valueAxisLabel) {
        this.valueAxisLabel = valueAxisLabel;
    }
    
    public String getValueAxisLabel() {
        return (String) valueBinding("valueAxisLabel", valueAxisLabel);
    }

    public void setLegend(boolean legend) {
        this.legend = legend;
    }
    
    public boolean getLegend() {
        return (Boolean) valueBinding("legend", legend);
    }

    public void setIs3D(boolean is3D) {
        this.is3D = true;
    }
    
    public boolean getIs3D() {
        return (Boolean) valueBinding("is3D", is3D);
    }
    
    @Override
    public void restoreState(FacesContext context, Object state)
    {
       Object[] values = (Object[]) state;
       super.restoreState(context, values[0]);      
       
       title             = (String) values[1];
       categoryAxisLabel = (String) values[2];
       valueAxisLabel    = (String) values[3];
       orientation       = (String) values[4];
       legend            = (Boolean) values[5];
       is3D              = (Boolean) values[6];
    }

    @Override
    public Object saveState(FacesContext context)
    {
       Object[] values = new Object[7];

       values[0] = super.saveState(context);
       values[1] = title;
       values[2] = categoryAxisLabel;
       values[3] = valueAxisLabel;
       values[4] = orientation;
       values[5] = legend;
       values[6] = is3D;

       return values;
    }
    
    @Override
    public void createDataset() {
        dataset = new DefaultCategoryDataset();
    }
    


//    @Override
//    public void configurePlot(Plot p) {
//        super.configurePlot(p);
//    }

    
    @Override
    public void configureRenderer(CategoryItemRenderer renderer){
        super.configureRenderer(renderer);
        if (renderer instanceof BarRenderer) { 
            configureRenderer((BarRenderer) renderer);
        }
    }
    
    
    public void configureRenderer(BarRenderer renderer) {
           
    }

    @Override
    public JFreeChart createChart(FacesContext context) {    
        if (!getIs3D()) {
            return ChartFactory.createBarChart(getTitle(),
                    getCategoryAxisLabel(),
                    getValueAxisLabel(),
                    dataset,
                    plotOrientation(getOrientation()),
                    getLegend(),
                    false,
                    false);
        } else {
            return ChartFactory.createBarChart3D(getTitle(),
                    getCategoryAxisLabel(),
                    getValueAxisLabel(),
                    dataset,
                    plotOrientation(getOrientation()),
                    getLegend(),
                    false,
                    false);
        }
    }

    @Override
    public Dataset getDataset() {        
        return dataset;
    }





}
