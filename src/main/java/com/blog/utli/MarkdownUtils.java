package com.blog.utli;



import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.AttributeProviderContext;
import org.commonmark.renderer.html.AttributeProviderFactory;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.*;

public class MarkdownUtils {
    /**
     * markdown格式转换成HTML格式
     * @param markdown
     * @return
     */
    public static String markdownToHtml(String markdown){
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);  // "<p>This is <em>Sparta</em></p>\n"
    }

    /**
     * 增加扩展【标题锚点，表格生成】
     * markdown转换HTML
     * @param markdown
     * @return
     */
    public static String markdownToHtmlExtensions(String markdown){
        //h标题生成id
        Set<Extension> headingAnchorExtension = Collections.singleton(HeadingAnchorExtension.create());
        //转换table的HTMl
        List<Extension> tableExtension = Arrays.asList(TablesExtension.create());
        Parser parser = Parser.builder().extensions(tableExtension).build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder()
                .extensions(headingAnchorExtension)
                .extensions(tableExtension)
                .attributeProviderFactory(new AttributeProviderFactory() {
                    @Override
                    public AttributeProvider create(AttributeProviderContext attributeProviderContext) {
                        return new CustomAttributeProvider();
                    }
                }).build();
        return renderer.render(document);
    }

    /**
     * 处理标签属性
     */
    static class CustomAttributeProvider implements AttributeProvider{

        @Override
        public void setAttributes(Node node, String s, Map<String, String> map) {
            //改变a标签的target属性为_blank
            //这个属于Link（如果node是一个a连接）
            if (node instanceof Link){
                map.put("target","_blank");
            }
            //添加class属性，值为ui celled table
            //node这个属于TableBlock(如果node是一个table)
            if (node instanceof TableBlock){
                map.put("class","ui celled table");
            }
        }
    }

    public static void main(String[] args) {
        String table = "| hello | hi    | 哈哈哈   |";
        String table1 = "| as  |  aa |  aa |   aa| aa  | aa  | aa  | aa  | aa  |\n" +
                "| ------------ | ------------ | ------------ | ------------ |\n" +
                "| a | a | a | a | a | a | a | a | a |\n" +
                "| a | a | a | a | a | a | a | a | a |\n" +
                "| a | a | a | a | a | a | a | a | a |\n" +
                "| a | a | a | a | a | a | a | a | a |\n" +
                "| a | a | a | a | a | a | a | a | a |";
        System.out.println(markdownToHtmlExtensions(table1));
    }
}
