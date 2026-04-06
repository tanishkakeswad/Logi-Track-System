package com.edutech.logisticsmanagementandtrackingsystem.service;

import com.edutech.logisticsmanagementandtrackingsystem.dto.ChatResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class RuleBasedChatService {

    private static final Pattern PUNCTUATION = Pattern.compile("[^a-z0-9\\s]");

    // Intent keywords in priority order
    private final LinkedHashMap<String, List<String>> intentKeywords = new LinkedHashMap<>();

    public RuleBasedChatService() {
        intentKeywords.put("GREETING", Arrays.asList("hi", "hello", "hey", "good morning", "good evening"));
        intentKeywords.put("HELP", Arrays.asList("help", "support", "how to", "guide", "instructions"));
        intentKeywords.put("LOGIN_REGISTER", Arrays.asList("login", "sign in", "register", "sign up", "password"));
        intentKeywords.put("ADD_CARGO", Arrays.asList("add cargo", "create cargo", "new cargo", "cargo create"));
        intentKeywords.put("ASSIGN_DRIVER", Arrays.asList("assign driver", "assign cargo", "driver assign"));
        intentKeywords.put("TRACK_CARGO", Arrays.asList("track", "cargo status", "awb", "order status", "tracking"));
        intentKeywords.put("UPLOAD_DOCUMENTS", Arrays.asList("upload document", "upload documents", "invoice", "permit", "file upload"));
        intentKeywords.put("DOWNLOAD_DOCUMENTS", Arrays.asList("download document", "view document", "open document", "documents"));
        intentKeywords.put("UPDATE_STATUS", Arrays.asList("update status", "change status", "delivered", "in transit", "order in-transit"));
    }

    public ChatResponse reply(String message, String context) {
        String normalized = normalize(message);

        if (normalized.isBlank()) {
            return new ChatResponse("Please type a message so I can help you 🙂", "EMPTY");
        }

        String role = getCurrentUserRole();
        String intent = matchIntent(normalized);

        String reply = buildReply(intent, role, context);
        return new ChatResponse(reply, intent);
    }

    private String normalize(String message) {
        if (message == null) return "";
        String m = message.trim().toLowerCase(Locale.ROOT);
        m = PUNCTUATION.matcher(m).replaceAll("");   // remove punctuation
        m = m.replaceAll("\\s+", " ");               // normalize spaces
        return m;
    }

    private String matchIntent(String normalized) {
        for (Map.Entry<String, List<String>> entry : intentKeywords.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (normalized.contains(keyword)) {
                    return entry.getKey();
                }
            }
        }
        return "UNKNOWN";
    }

    private String getCurrentUserRole() {
    try {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null) return "GUEST";

        String authority = auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("GUEST");

        if ("ROLE_ANONYMOUS".equalsIgnoreCase(authority)) {
            return "GUEST";
        }
        return authority;
    } catch (Exception e) {
        return "GUEST";
    }
}

    private String buildReply(String intent, String role, String context) {

        switch (intent) {
            case "GREETING":
                return "Hi! 👋 I’m LogiBot. I can help with cargo, documents, assignment, and tracking.\n"
                     + "Try: “How to upload documents?” or “How to track cargo?”";

            case "HELP":
                return "Here are things I can help you with:\n"
                     + "• Add Cargo (Business)\n"
                     + "• Assign Cargo to Driver (Business)\n"
                     + "• Upload / Download Cargo Documents\n"
                     + "• Track Cargo Status (Customer/Business)\n"
                     + "• Update Delivery Status (Driver)\n\n"
                     + "Ask me any of these in simple words 🙂";

            case "LOGIN_REGISTER":
                return "Login/Register help:\n"
                     + "• Register a user with role (BUSINESS / DRIVER / CUSTOMER)\n"
                     + "• Login to receive JWT token\n"
                     + "• Token is required for secured APIs\n\n"
                     + "If login fails: check username/password and role.";

            case "ADD_CARGO":
                if ("BUSINESS".equals(role)) {
                    return "To add cargo (Business):\n"
                         + "1) Open Manage Cargo → Add New Cargo\n"
                         + "2) Enter Content, Size, Status\n"
                         + "3) (Optional) Upload documents (PDF/JPG/DOCX)\n"
                         + "4) Submit\n\n"
                         + "Cargo is created and documents get linked permanently.";
                }
                return "Only BUSINESS users can create cargo. Please login as BUSINESS.";

            case "ASSIGN_DRIVER":
                if ("BUSINESS".equals(role)) {
                    return "To assign cargo to a driver:\n"
                         + "1) Go to Manage Cargo → Cargo List\n"
                         + "2) Click Assign on a cargo\n"
                         + "3) Select a Driver\n"
                         + "4) Save\n\n"
                         + "Now the driver can see that cargo in their dashboard.";
                }
                return "Only BUSINESS users can assign cargos to drivers.";

            case "TRACK_CARGO":
                if ("CUSTOMER".equals(role)) {
                    return "To track cargo (Customer):\n"
                         + "1) Open Track Cargo / View Cargo Status page\n"
                         + "2) Enter AWB number (Cargo ID)\n"
                         + "3) Click Search\n\n"
                         + "You’ll see the latest delivery status.";
                } else if ("BUSINESS".equals(role)) {
                    return "To track cargo (Business):\n"
                         + "1) Manage Cargo → Search by AWB (Cargo ID)\n"
                         + "2) View status updates made by driver\n\n"
                         + "Tip: Track also via Customer cargo-status API if needed.";
                } else if ("DRIVER".equals(role)) {
                    return "As a Driver, you can view assigned cargos and update their status.\n"
                         + "Tracking is mainly for Business/Customer.";
                }
                return "You can track cargo using AWB/Cargo ID in the tracking page.";

            case "UPLOAD_DOCUMENTS":
                if ("BUSINESS".equals(role)) {
                    return "Document upload (Business):\n"
                         + "1) While creating cargo, choose one/multiple files\n"
                         + "2) Files are sent using FormData\n"
                         + "3) Backend stores files in: uploads/cargo/{cargoId}/documents/\n"
                         + "4) Only metadata is stored in DB (cargo_document table)\n\n"
                         + "If upload fails: file may exceed size limit or unsupported type.";
                }
                return "Only BUSINESS users can upload cargo documents.";

            case "DOWNLOAD_DOCUMENTS":
                if ("DRIVER".equals(role)) {
                    return "To view/download documents (Driver):\n"
                         + "1) Open Assigned Cargo page\n"
                         + "2) Under Documents column click View\n"
                         + "3) For PDF/Image it opens; for DOCX it downloads\n\n"
                         + "Note: Secure download uses JWT header (Blob download).";
                } else if ("BUSINESS".equals(role)) {
                    return "To view/download documents (Business):\n"
                         + "1) Open Manage Cargo list\n"
                         + "2) Look for Documents column\n"
                         + "3) Click View to open/download\n\n"
                         + "Documents are securely served via backend.";
                }
                return "Documents are available only to authorized users (Business/Driver).";

            case "UPDATE_STATUS":
                if ("DRIVER".equals(role)) {
                    return "To update delivery status (Driver):\n"
                         + "1) Open Assigned Cargo\n"
                         + "2) Click Update Status\n"
                         + "3) Choose Order In-transit or Order Delivered\n"
                         + "4) Save\n\n"
                         + "Business/Customer can track the updated status.";
                }
                return "Only DRIVER users can update cargo delivery status.";

            default:
                // UNKNOWN
                return "Sorry, I didn’t understand that 😅\n"
                     + "Try asking about:\n"
                     + "• add cargo\n"
                     + "• assign driver\n"
                     + "• upload documents\n"
                     + "• download documents\n"
                     + "• track cargo\n"
                     + "• update status";
        }
    }
}