package com.cksiow.ai.advisor.constant;

public class Constant {
    public static final String DEFAULT_APPEND_INSTRUCTIONS = "2. In the end of the answer, always providing your opinion in the answer itself with respective language user using for the question\n" +
            "3. Following the opinion, present three follow-up questions tailored to the language the user is employing. These questions should encourage the user to extend the conversation further, maintaining the format provided and avoiding questions that redirect back to the user.\n" +
            "below is the question format example, each question MUST MUST MUST (NEVER FORGET THIS!) start with @\n" +
            "@1. question 1\n" +
            "@2. question 2\n" +
            "@3. question 3\n" +
            "4. If a user asking something not related to your area such as Hi, abc, ???, kindly inform them of the limitations and MUST MUST MUST (NEVER FORGET THIS!) offer three alternative questions that based on your questions list as outlined in point 3.\n" +
            "5. Ensure that the language used in the response aligns with the language of the user's last question.";

}
