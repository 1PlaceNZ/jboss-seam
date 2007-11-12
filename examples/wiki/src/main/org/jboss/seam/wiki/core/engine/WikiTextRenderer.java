package org.jboss.seam.wiki.core.engine;

import java.util.List;

/**
 * Called by the WikiTextParser to render [A Link=>Target] and [<=MacroName].
 *
 * @author Christian Bauer
 */
public interface WikiTextRenderer {

    public String renderInlineLink(WikiLink inlineLink);
    public String renderExternalLink(WikiLink externalLink);
    public String renderThumbnailImageInlineLink(WikiLink inlineLink);
    public String renderFileAttachmentLink(int attachmentNumber, WikiLink attachmentLink);
    public String renderMacro(String macroName);

    public String renderParagraphOpenTag();
    public String renderPreformattedOpenTag();
    public String renderBlockquoteOpenTag();
    public String renderHeadline1Opentag();
    public String renderHeadline2OpenTag();
    public String renderHeadline3OpenTag();
    public String renderHeadline4OpenTag();
    public String renderOrderedListOpenTag();
    public String renderOrderedListItemOpenTag();
    public String renderUnorderedListOpenTag();
    public String renderUnorderedListItemOpenTag();

    public void setAttachmentLinks(List<WikiLink> attachmentLinks);
    public void setExternalLinks(List<WikiLink> externalLinks);
}
