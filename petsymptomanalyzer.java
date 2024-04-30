import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;
import java.util.Random;


class Pet {
    JFrame frame;
    JLabel questionLabel;
    JTextField answerField;
    JLabel dr;
    JButton startButton;
    JButton previousButton;
    JButton nextButton;
    JLabel tipLabel;
    JLabel tipTextLabel;
    JLabel dogImageLabel;
    Map<String, String[]> questions;
    Map<String, String[]> responses;
    String currentDisease;
    Map<String, Integer> diseaseQuestionIndices;
    String[] tips;
    int tipIndex = 0;

    public Pet() {
        initialize();
    }

    void createsuggestdoctorbutton() {
        JButton suggestDoctorButton = new JButton("Suggest Doctor in my City");
        suggestDoctorButton.setBounds(10, 200, 200, 30);
        suggestDoctorButton.addActionListener(e -> suggestdoctor());
        frame.add(suggestDoctorButton);
        frame.revalidate();
    }

    void suggestdoctor() {
        JTextField parentNameField = new JTextField();
        JTextField petNameField = new JTextField();
        JTextField petAgeField = new JTextField();
        JTextField petBreedField = new JTextField();
        JTextField contactNoField = new JTextField();
        JTextField cityField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(7, 2));
        panel.add(new JLabel("Pet Parent's Name:"));
        panel.add(parentNameField);
        panel.add(new JLabel("Pet's Name:"));
        panel.add(petNameField);
        panel.add(new JLabel("Pet's Age(IN MONTHS):"));
        panel.add(petAgeField);
        panel.add(new JLabel("Pet's Breed:"));
        panel.add(petBreedField);
        panel.add(new JLabel("Contact Number:"));
        panel.add(contactNoField);
        panel.add(new JLabel("City:"));
        panel.add(cityField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Enter Pet Details",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String parentName = parentNameField.getText();
            String petName = petNameField.getText();
            int petAge = Integer.parseInt(petAgeField.getText());
            String petBreed = petBreedField.getText();
            String contactNo = contactNoField.getText();
            String city = cityField.getText();

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");

                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/doctor_directory", "root", "Snehitha11@rafi");

                String insertSql = "INSERT INTO pet_details (parent_name, pet_name, pet_age, pet_breed, contact_no, city) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement insertStatement = conn.prepareStatement(insertSql);
                insertStatement.setString(1, parentName);
                insertStatement.setString(2, petName);
                insertStatement.setInt(3, petAge);
                insertStatement.setString(4, petBreed);
                insertStatement.setString(5, contactNo);
                insertStatement.setString(6, city);
                insertStatement.executeUpdate();

                String selectSql = "SELECT * FROM doctors d JOIN cities c ON d.city_id = c.city_id WHERE c.city_name = '" + city + "'";
                PreparedStatement selectStatement = conn.prepareStatement(selectSql);


                ResultSet rs = selectStatement.executeQuery();

                StringBuilder doctorList = new StringBuilder();
                while (rs.next()) {
                    int doctorId = rs.getInt("doctor_id");
                    String doctorName = rs.getString("doctor_name");
                    double rating = rs.getDouble("rating");
                    String phoneNumber = rs.getString("phone_number");
                    String address = rs.getString("address");
                    doctorList.append("Doctor ID: ").append(doctorId).append("\n")
                            .append("Name: ").append(doctorName).append("\n")
                            .append("Rating: ").append(rating).append("\n")
                            .append("Phone Number: ").append(phoneNumber).append("\n")
                            .append("Address: ").append(address).append("\n\n");
                }
                if (!doctorList.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Doctors in " + city + ":\n" + doctorList, "Doctors", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "No doctors found in " + city, "No Doctors", JOptionPane.INFORMATION_MESSAGE);
                }

                rs.close();
                selectStatement.close();
                insertStatement.close();
                conn.close();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error processing request. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

     void disableNavigationButtons() {
        previousButton.setEnabled(false);
        nextButton.setEnabled(false);
    }

    void initialize() {
        frame = new JFrame("Pet Symptom Analyzer");
        frame.setSize(800, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        dr = new JLabel("LETS START...");
        dr.setBounds(10, 10, 100, 20);
        frame.add(dr);



        questions = new HashMap<>();
        questions.put(" parvovirus", new String[]{
                "Is your pet experiencing loss of appetite? (yes/no)",
                "Is your pet vomiting frequently? (yes/no)",
                "Is your pet having bloody diarrhea? (yes/no)",
                "Is your pet lethargic or lacking energy? (yes/no)",
        });
        questions.put("distemper", new String[]{
                "Is your pet experiencing a fever? (yes/no)",
                "Is your pet coughing? (yes/no)",
                "Does your pet have watery eye discharge? (yes/no)",
                "Is your pet experiencing diarrhea with mucus? (yes/no)"
                ,

        });

        questions.put("kennel cough", new String[]{
                "Is your pet experiencing a persistent dry cough? (yes/no)",
                "Is your pet sneezing frequently? (yes/no)",
                "Is your pet having nasal discharge? (yes/no)",
                "Is your pet active and playful? (yes/no)"
        });
        questions.put("rabies", new String[]{
                "Is your pet displaying aggressive behavior? (yes/no)",
                "Is your pet having difficulty swallowing? (yes/no)",
                "Is your pet avoiding water? (yes/no)",
                "Has your pet vomited? (yes/no)"
        });
        responses = new HashMap<>();
        for (String disease : questions.keySet()) {
            responses.put(disease, new String[questions.get(disease).length]);
        }

        diseaseQuestionIndices = new HashMap<>();
        for (String disease : questions.keySet()) {
            diseaseQuestionIndices.put(disease, 0);
        }

        startButton = new JButton("Start");
        startButton.setBounds(10, 50, 100, 30);
        startButton.addActionListener(e -> startQuestionnaire());
        frame.add(startButton);

        questionLabel = new JLabel("Welcome to the Pet Symptom Analyzer");
        questionLabel.setBounds(10, 100, 500, 20);
        frame.add(questionLabel);

        answerField = new JTextField();
        answerField.setBounds(10, 130, 200, 20);
        frame.add(answerField);

        previousButton = new JButton("Previous");
        previousButton.setBounds(10, 160, 100, 30);
        previousButton.addActionListener(e -> previousquestion());
        frame.add(previousButton);

        nextButton = new JButton("Next");
        nextButton.setBounds(120, 160, 100, 30);
        nextButton.addActionListener(e -> nextQuestion());
        frame.add(nextButton);

        tipLabel = new JLabel("Tip for a Healthy Dog/Puppy:");
        tipLabel.setBounds(10, 250, 200, 20);
        frame.add(tipLabel);

        tipTextLabel = new JLabel("");
        tipTextLabel.setBounds(10, 280, 400, 20);
        frame.add(tipTextLabel);

        ImageIcon dogImageIcon = new ImageIcon("dog_image.jpg");
        Image dogImage = dogImageIcon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
        dogImageIcon = new ImageIcon(dogImage);
        dogImageLabel = new JLabel(dogImageIcon);
        dogImageLabel.setBounds(400, 0, 400, 400);
        frame.add(dogImageLabel);

        frame.getContentPane().setBackground(new Color(173, 216, 230));
        frame.setVisible(true);

        tips = new String[]{
                "Regular exercise is essential for your dog's health and happiness.",
                "Ensure your dog has a balanced diet with high-quality dog food.",
                "Regular vet check-ups are crucial for preventive care.",
                "Train your dog with patience and positive reinforcement.",
                "Keep your dog hydrated, especially in hot weather."
        };

        Timer timer = new Timer(5000, e -> updateTip());
        timer.start();
    }

     void updateTip() {
        Random random = new Random();
        int randomIndex = random.nextInt(tips.length);
        tipTextLabel.setText(tips[randomIndex]);
    }

     void startQuestionnaire() {
        currentDisease = questions.keySet().iterator().next();
        askQuestion();
        startButton.setEnabled(false);
    }

    void askQuestion() {
        if (currentDisease != null) {
            String[] currentQuestions = questions.get(currentDisease);
            int currentIndex = diseaseQuestionIndices.get(currentDisease);
            questionLabel.setText(currentQuestions[currentIndex]);
        }
    }

     void previousquestion() {
        if (currentDisease != null) {
            int currentIndex = diseaseQuestionIndices.get(currentDisease);
            if (currentIndex > 0) {
                diseaseQuestionIndices.put(currentDisease, currentIndex - 1);
                askQuestion();
            }
        }
    }

     void nextQuestion() {
        if (currentDisease != null) {
            String[] currentResponses = responses.get(currentDisease);
            int currentIndex = diseaseQuestionIndices.get(currentDisease);
            currentResponses[currentIndex] = answerField.getText().toLowerCase();
            answerField.setText("");

            int lastIndex = questions.get(currentDisease).length - 1;
            if (currentIndex < lastIndex) {
                diseaseQuestionIndices.put(currentDisease, currentIndex + 1);
                askQuestion();
            } else {
                nextdisease();
            }
        }
    }

     void nextdisease() {
        int currentIndex = new java.util.ArrayList<>(questions.keySet()).indexOf(currentDisease);
        int lastIndex = questions.size() - 1;
        if (currentIndex < lastIndex) {
            currentDisease = questions.keySet().toArray(new String[0])[currentIndex + 1];
            diseaseQuestionIndices.put(currentDisease, 0);
            askQuestion();
        } else {
            submit();
        }
    }

    void submit() {
        predictcondition();
        createsuggestdoctorbutton();
        disableNavigationButtons();
    }

     void predictcondition() {
        String mostLikelyDisease = null;
        int maxYesCount = 0;

        for (String disease : responses.keySet()) {
            String[] symptoms = responses.get(disease);
            int yesCount = 0;
            for (String symptom : symptoms) {
                if ("yes".equalsIgnoreCase(symptom)) {
                    yesCount++;
                }
            }
            if (yesCount > maxYesCount) {
                maxYesCount = yesCount;
                mostLikelyDisease = disease;
            }
        }

        if (mostLikelyDisease != null) {
            String message = "Your pet's symptoms may indicate " + mostLikelyDisease + ". Consult a veterinarian for a proper diagnosis.";
            JOptionPane.showMessageDialog(frame, message, "Condition Prediction", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "Based on your responses, your pet seems to be in good health.", "Good Health", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new Pet();
    }
}
