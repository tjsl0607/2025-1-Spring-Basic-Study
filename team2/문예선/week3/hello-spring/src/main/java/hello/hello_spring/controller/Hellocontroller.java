package hello.hello_spring.controller;

import ch.qos.logback.core.model.Model;
import io.micrometer.observation.transport.Propagator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {
    @GetMapping("hello")
    public String hello(Model model) {
        model.addAttribute("data", "hello!!");
        return "hello";
    }

    @GetMapping("hello-mvc")
    public String helloMvc(@RequestParam(value= "name", required = true) String name, Model model) {
        model.addAttribute(attributeName: "name", name)
        return "hello-template"
    }

    @GetMapping("hello-string")
    @ResponseBody
    public String hellostring(@RequestParam("name") String name) {
        return "hello" + name;
    }

    @GetMapping("hello-api")
    @ResponseBody
    public Hello helloapi (@RequestParam("name") Stirng name) {
        Hello hello = new Hello();
    }
}

    static class Hello {

        private String name;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;

        }
}
