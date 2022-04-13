package mobileapp.ctemplar.com.ctemplarapp.utils;

import static org.jsoup.nodes.Document.OutputSettings.Syntax.html;

import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Evaluator;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class HtmlUtils {
    private static final PolicyFactory htmlPolicyFactory = new HtmlPolicyBuilder()
            .allowElements("a", "b", "br", "div", "font", "h1", "h2", "h3", "h4", "h5", "h6", "hr", "img", "label", "li", "ol", "p", "span", "strong", "table", "td", "th", "tr", "u", "ul", "i", "pre")
            .allowAttributes("style").onElements("a", "b", "br", "div", "font", "img", "label", "li", "ol", "p", "span", "strong", "table", "td", "th", "tr", "u", "ul")
            .allowAttributes("align", "dir", "id", "style").onElements("h1", "h2", "h3", "h4", "h5", "h6")
            .allowAttributes("dir").onElements("div", "li", "ol", "p", "table", "td", "th", "tr", "ul")
            .allowAttributes("align").onElements("div", "hr", "img", "p", "table", "td", "th", "tr")
            .allowAttributes("width").onElements("hr", "img", "table", "td", "th")
            .allowAttributes("cellpadding", "cellspacing").onElements("table")
            .allowAttributes("bgcolor").onElements("table", "td", "th", "tr")
            .allowAttributes("hspace", "vspace", "usemap").onElements("img")
            .allowAttributes("height").onElements("img", "td", "th")
            .allowAttributes("valign").onElements("td", "th", "tr")
            .allowAttributes("border").onElements("img", "table")
            .allowAttributes("frame, rules").onElements("table")
            .allowAttributes("color", "face").onElements("font")
            .allowAttributes("href", "style", "target").onElements("a")
            .allowAttributes("colspan").onElements("td", "th")
            .allowAttributes("size").onElements("font", "hr")
            .allowAttributes("scope").onElements("td", "th")
            .allowAttributes("lang").onElements("td", "th")
            .allowAttributes("type").onElements("li", "ol")
            .allowAttributes("abbr").onElements("td", "th")
            .allowAttributes("background").onElements("th")
            .allowAttributes("id").onElements("label")
            .allowAttributes("src").onElements("img")
            .allowUrlProtocols("http", "https")
            .requireRelNofollowOnLinks()
            .toFactory();

    public static String toHtml(Spannable text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.toHtml(text, Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
        } else {
            return Html.toHtml(text);
        }
    }

    public static Spanned fromHtml(String text) {
        if (TextUtils.isEmpty(text)) {
            return new SpannableString("");
        }
        Document document = Jsoup.parse(text);
        for (Element element : document.select("script,style,img"))
            element.remove();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return Html.fromHtml(document.html());
        } else {
            // FROM_HTML_MODE_COMPACT
            return Html.fromHtml(document.html(), Html.FROM_HTML_MODE_LEGACY);
        }
    }

    public static boolean isContainImages(String text) {
        return Jsoup.parse(text).select("img").size() > 0;
    }

    public static boolean isHtml(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        String fromHtml = fromHtml(text).toString();
        return !text.equals(fromHtml);
    }

    public static String sanitizeHTML(String untrustedHTML) {
        return htmlPolicyFactory.sanitize(untrustedHTML);
    }

    public static byte[] formatHtml(String plainHTML) {
        String formattedHtml = "<style type=\"text/css\">*{width:auto;max-width:100%;}</style>"
                + sanitizeHTML(plainHTML);
        return formattedHtml.getBytes();
    }
}
