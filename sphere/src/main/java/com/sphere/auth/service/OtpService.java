package com.sphere.auth.service;

import com.sphere.auth.Otp;
import com.sphere.auth.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;
    private final JavaMailSender mailSender;

    @Transactional
    public void sendOtp(String email) {
        otpRepository.deleteByEmail(email);

        String code = String.format("%06d", new Random().nextInt(999999));

        Otp otp = Otp.builder()
                .email(email)
                .code(code)
                .used(false)
                .build();

        otpRepository.save(otp);
        sendOtpEmail(email, code);
    }

    @Transactional
    public boolean verifyOtp(String email, String code) {
        Otp otp = otpRepository.findByEmailAndCodeAndUsedFalse(email, code)
                .orElseThrow(() -> new RuntimeException("Invalid or expired OTP"));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired");
        }

        otp.setUsed(true);
        otpRepository.save(otp);
        return true;
    }

    private void sendOtpEmail(String email, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply.sphereauth@gmail.com");
            helper.setTo(email);
            helper.setSubject("Your Sphere Verification Code");
            helper.setText(buildEmailTemplate(code), true);

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
        }
    }

    private String buildEmailTemplate(String code) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <meta http-equiv="X-UA-Compatible" content="IE=edge">
                  <title>Verify your Sphere account</title>
                  <style>
                    @media only screen and (max-width: 600px) {
                      .email-wrapper { padding: 24px 12px !important; }
                      .email-card    { width: 100%% !important; border-radius: 12px !important; }
                      .email-pad     { padding: 28px 22px !important; }
                      .otp-code      { font-size: 36px !important; letter-spacing: 8px !important; }
                      .otp-box       { padding: 22px 16px !important; }
                      .header-pad    { padding: 28px 22px 22px !important; }
                      .footer-pad    { padding: 18px 22px !important; }
                    }
                  </style>
                </head>
                <body style="margin:0;padding:0;background-color:#080808;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Helvetica,Arial,sans-serif;-webkit-font-smoothing:antialiased;">

                  <table width="100%%" cellpadding="0" cellspacing="0" class="email-wrapper" style="background-color:#080808;padding:56px 20px;">
                    <tr>
                      <td align="center">
                        <table width="540" cellpadding="0" cellspacing="0" class="email-card" style="background-color:#0d0d0d;border-radius:20px;border:1px solid rgba(255,255,255,0.07);overflow:hidden;">

                          <!-- HEADER -->
                          <tr>
                            <td class="header-pad" style="padding:40px 40px 32px;text-align:center;border-bottom:1px solid rgba(255,255,255,0.05);">
                              <h1 style="margin:0;color:#ffffff;font-size:34px;font-weight:300;letter-spacing:0.18em;font-family:Georgia,'Times New Roman',serif;">Sphere</h1>
                              <div style="width:30px;height:1px;background:rgba(255,255,255,0.15);margin:15px auto 13px;"></div>
                              <p style="margin:0;color:rgba(255,255,255,0.2);font-size:10px;letter-spacing:0.26em;text-transform:uppercase;font-family:Helvetica,Arial,sans-serif;">Your world, your communities</p>
                            </td>
                          </tr>

                          <!-- BODY -->
                          <tr>
                            <td class="email-pad" style="padding:44px 40px 40px;">

                              <h2 style="margin:0 0 10px;color:#ffffff;font-size:22px;font-weight:600;letter-spacing:-0.02em;line-height:1.3;">Verify your email</h2>
                              <p style="margin:0 0 36px;color:rgba(255,255,255,0.32);font-size:14px;line-height:1.75;">
                                Use the verification code below to confirm your email address and complete your Sphere registration. This code is valid for <span style="color:rgba(255,255,255,0.6);font-weight:500;">10 minutes</span>.
                              </p>

                              <!-- OTP BOX -->
                              <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:32px;">
                                <tr>
                                  <td class="otp-box" style="background:rgba(255,255,255,0.03);border:1px solid rgba(255,255,255,0.09);border-radius:16px;padding:32px 24px;text-align:center;">
                                    <p style="margin:0 0 16px;color:rgba(255,255,255,0.25);font-size:10px;text-transform:uppercase;letter-spacing:0.22em;font-family:Helvetica,Arial,sans-serif;">One-Time Verification Code</p>
                                    <p class="otp-code" style="margin:0;color:#ffffff;font-size:52px;font-weight:700;letter-spacing:16px;font-family:Georgia,'Times New Roman',serif;line-height:1;text-indent:16px;">%s</p>
                                    <div style="width:56px;height:1px;background:rgba(255,255,255,0.14);margin:20px auto 0;"></div>
                                  </td>
                                </tr>
                              </table>

                              <!-- META INFO -->
                              <table width="100%%" cellpadding="0" cellspacing="0">
                                <tr>
                                  <td style="background:rgba(255,255,255,0.02);border:1px solid rgba(255,255,255,0.05);border-radius:11px;padding:16px 20px;">
                                    <table width="100%%" cellpadding="0" cellspacing="0">
                                      <tr>
                                        <td width="20" style="vertical-align:top;padding-top:1px;">
                                          <span style="font-size:13px;">⏱</span>
                                        </td>
                                        <td style="padding-left:8px;">
                                          <p style="margin:0;color:rgba(255,255,255,0.3);font-size:13px;line-height:1.5;">
                                            Code expires in <strong style="color:rgba(255,255,255,0.65);font-weight:500;">10 minutes</strong>. Do not share it with anyone.
                                          </p>
                                        </td>
                                      </tr>
                                    </table>
                                    <div style="height:1px;background:rgba(255,255,255,0.05);margin:12px 0;"></div>
                                    <table width="100%%" cellpadding="0" cellspacing="0">
                                      <tr>
                                        <td width="20" style="vertical-align:top;padding-top:1px;">
                                          <span style="font-size:13px;">🔒</span>
                                        </td>
                                        <td style="padding-left:8px;">
                                          <p style="margin:0;color:rgba(255,255,255,0.22);font-size:12px;line-height:1.55;">
                                            If you didn't request this, you can safely ignore this email. Your account is secure.
                                          </p>
                                        </td>
                                      </tr>
                                    </table>
                                  </td>
                                </tr>
                              </table>

                            </td>
                          </tr>

                          <!-- FOOTER -->
                          <tr>
                            <td class="footer-pad" style="padding:22px 40px 26px;border-top:1px solid rgba(255,255,255,0.05);text-align:center;">
                              <p style="margin:0 0 6px;color:rgba(255,255,255,0.12);font-size:11px;letter-spacing:0.05em;font-family:Helvetica,Arial,sans-serif;">&#169; 2026 Sphere. All rights reserved.</p>
                              <p style="margin:0;color:rgba(255,255,255,0.08);font-size:11px;font-family:Helvetica,Arial,sans-serif;">This is an automated message &#8212; please do not reply.</p>
                            </td>
                          </tr>

                        </table>
                      </td>
                    </tr>
                  </table>

                </body>
                </html>
                """.formatted(code);
    }
}