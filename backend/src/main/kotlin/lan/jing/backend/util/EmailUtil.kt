package lan.jing.backend.util

import jakarta.annotation.Resource
import jakarta.mail.MessagingException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.*


@Component
class EmailUtil {
    @Resource
    lateinit var javaMailSender: JavaMailSender

    @Resource
    lateinit var templateEngine: TemplateEngine
    var randObj = Random()
    var code = ""
    private fun generateCode(): String {
        val code = (100000 + randObj.nextInt(900000)).toString()
        this.code = code
        return code
    }
    fun sendCode(email:String){
        val context = Context()
        context.setVariable("verifyCode", generateCode().split("").filter {
            it != ""
        })
        val process = templateEngine.process("EmailVerificationCode.html", context)
        val mimeMessage = javaMailSender.createMimeMessage()
        try {
            val helper = MimeMessageHelper(mimeMessage, true)
            helper.setSubject("【药品管理系统】验证码")
            helper.setFrom("2116996207@qq.com")
            helper.setTo(email)
            helper.setSentDate(Date())
            helper.setText(process, true)
        } catch (_: MessagingException) {

        }
        javaMailSender.send(mimeMessage)
    }
}