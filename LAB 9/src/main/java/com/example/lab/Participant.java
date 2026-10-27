package com.example.lab;

public class Participant {
    private final String name;
    private final String email;
    private final int age;
    private final String subject;
    private final int examsCount;
    private final int sum;

    public Participant(String name, String email, int age, String subject, int examsCount, int sum) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.subject = subject;
        this.examsCount = examsCount;
        this.sum = sum;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getAge() { return age; }
    public String getSubject() { return subject; }
    public int getExamsCount() { return examsCount; }
    public int getSum() { return sum; }
}