package com.ticketmaster.portal.webui.client.layout;

import com.ticketmaster.portal.webui.client.TicketMaster;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.HTMLPane;

public class Footer extends HTMLPane {
	public Footer() {
		setWidth100();
		setHeight("81");
		refresh(null, false);
	}

	public void refresh(Record record, boolean isLoggedIn) {
		setContents("<div class=\"footer_wrapper\">" + "<table cellpadding=\"0\" cellspacing=\"0\" class=\"footer\">"
				+ "<tbody><tr>"
				+ "<td class=\"footer_hot_line english\" align=\"left\" valign=\"middle\" style=\"width: 280px;\">"
				+ getAccountManagerDetails(record, isLoggedIn)
				+ "</td>"
				+ "</td>"
				+ " <td class=\"footer_hot_line english\" align=\"left\" valign=\"middle\">"
				+ "     <h3>"
				+ TicketMaster.constants.hot_line()
				+ "</h3><strong style=\"color:#f80000\">032 217 00 00</strong> ან <strong style=\"color:#f80000\">110011</strong>&nbsp;&nbsp;-&nbsp;&nbsp;"
				+ TicketMaster.constants.any_network()
				+ "<br><a href=\"mailto:office@magticom.ge\" class=\"english\">office@magticom.ge</a>"
				+ "</td>"
				+ "<td class=\"footer_logos\" valign=\"middle\">"
				+ "   <a href=\"http://www.bali.ge\" target=\"_blank\" onmouseover=\"bali.src = 'images/logos/bali_color.png'\" onmouseout=\"bali.src = 'images/logos/bali_bw.png'\">"
				+ "     <img name=\"bali\" src=\"images/logos/bali_bw.png\">"
				+ " </a>"
				+ " <a href=\"http://www.bani.ge\" target=\"_blank\" onmouseover=\"bani.src = 'images/logos/bani_color.png'\" onmouseout=\"bani.src = 'images/logos/bani_bw.png'\">"
				+ "     <img name=\"bani\" src=\"images/logos/bani_bw.png\">"
				+ " </a>"
				+ " <a href=\"http://www.magtifix.ge\" target=\"_blank\" onmouseover=\"magtifix.src = 'images/logos/magtifix_color.png'\" onmouseout=\"magtifix.src = 'images/logos/magtifix_bw.png'\">"
				+ "  <img name=\"magtifix\" src=\"images/logos/magtifix_bw.png\">"
				+ " </a>"
				+ " <a href=\"http://www.magtisat.ge\" target=\"_blank\" onmouseover=\"magtisat.src = 'images/logos/magtisat_color.png'\" onmouseout=\"magtisat.src = 'images/logos/magtisat_bw.png'\">"
				+ "  <img name=\"magtisat\" src=\"images/logos/magtisat_bw.png\">"
				+ " </a>"
				+ " <td class=\"footer_rights\" align=\"right\" valign=\"middle\">"
				+ " <a href=\"http://www.facebook.com/MagtiFun\" target=\"_blank\" onmouseover=\"facebook.src = 'images/socials/fb_color.gif'\" onmouseout=\"facebook.src = 'images/socials/fb_bw.gif'\">"
				+ " <img name=\"facebook\" src=\"images/socials/fb_bw.gif\">"
				+ " </a> "
				+ " <a href=\"https://plus.google.com/104788239187870510145/posts\" target=\"_blank\" onmouseover=\"google.src = 'images/socials/gp_color.gif'\" onmouseout=\"google.src = 'images/socials/gp_bw.gif'\">"
				+ " <img name=\"google\" src=\"images/socials/gp_bw.gif\">"
				+ " </a>"
				+ " <a href=\"https://twitter.com/MagtiCom\" target=\"_blank\" onmouseover=\"twitter.src = 'images/socials/twitter_color.gif'\" onmouseout=\"twitter.src = 'images/socials/twitter_bw.gif'\">"
				+ " <img name=\"twitter\" src=\"images/socials/twitter_bw.gif\">"
				+ " </a>"
				+ " <a href=\"http://www.youtube.com/user/magticomge/\" target=\"_blank\" onmouseover=\"youtube.src = 'images/socials/youtube_color.gif'\" onmouseout=\"youtube.src = 'images/socials/youtube_bw.gif'\">"
				+ " <img name=\"youtube\" src=\"images/socials/youtube_bw.gif\">"
				+ " </a>"
				+ " <!-- <a href=\"index.php?section=21&lang=GEO\" onMouseOver=\"faq.src='images/socials/faq_color.gif'\" onMouseOut=\"faq.src='images/socials/faq_bw.gif'\">"
				+ " <img name=\"faq\" src=\"images/socials/faq_bw.gif\" />"
				+ " </a> -->"
				+ " <br>"
				+ " © <a href=\"http://www.magticom.ge/index.php?lang=GEO\" target=\"_blank\" class=\"footer_name\">"
				+ TicketMaster.constants.ltd_magticom()
				+ "</a><br>"
				+ " "
				+ TicketMaster.constants.magticom_copyright() + "</td>" + " </tr>" + " </tbody></table></div>");
	}

	private String getAccountManagerDetails(Record record, boolean isLoggedIn) {
		String accManName = "";
		String accManPhone = "";
		String accManEmail = "";
		if (record != null) {
			String rAccManName = record.getAttribute("acc_man_name");
			String rAccManPhone = record.getAttribute("phone");
			String rAccManEmail = record.getAttribute("email");
			if (rAccManName != null)
				accManName = rAccManName;
			if (rAccManPhone != null)
				accManPhone = rAccManPhone;
			if (rAccManEmail != null)
				accManEmail = rAccManEmail;
			return TicketMaster.constants.contact_person() + " : " + "<strong style=\"color:#f80000\">" + accManName
					+ "</strong>" + "<br>" + TicketMaster.constants.phone() + " :<strong style=\"color:#f80000\">"
					+ accManPhone + "</strong>" + "<br>" + TicketMaster.constants.mail() + " :<a href=\"mailto:"
					+ accManEmail + "\" class=\"english\">" + accManEmail + "</a>";
		} else if (isLoggedIn) {
			return "<strong style=\"color:#f80000\">" + TicketMaster.constants.please_select_contract()
					+ "</strong>";
		} else {
			return "<strong style=\"color:#f80000\">MAGTICOM</strong>";
		}
	}
}
