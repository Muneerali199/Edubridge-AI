-- Sample Instructors
INSERT INTO instructor (id, name, email, bio, expertise, rating, total_students, profile_image_url, created_at, updated_at)
VALUES 
('550e8400-e29b-41d4-a716-446655440001', 'Dr. Sarah Johnson', 'sarah.johnson@edubridge.com', 'AI and Machine Learning expert with 10+ years of experience', 'Artificial Intelligence, Machine Learning', 4.8, 5000, 'https://ui-avatars.com/api/?name=Sarah+Johnson&background=6366f1&color=fff', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440002', 'Mark Wilson', 'mark.wilson@edubridge.com', 'Full-stack developer and instructor passionate about modern web technologies', 'Web Development, JavaScript, React', 4.9, 8000, 'https://ui-avatars.com/api/?name=Mark+Wilson&background=10b981&color=fff', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440003', 'Emily Chen', 'emily.chen@edubridge.com', 'Data scientist helping students master data analysis and visualization', 'Data Science, Python, Statistics', 4.7, 3500, 'https://ui-avatars.com/api/?name=Emily+Chen&background=f59e0b&color=fff', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample Courses
INSERT INTO course (id, title, description, instructor_id, level, status, thumbnail_url, price, discount_price, duration_hours, total_lectures, total_enrollments, average_rating, total_reviews, voice_enabled, voice_language, ai_tutor_enabled, created_at, updated_at)
VALUES
('660e8400-e29b-41d4-a716-446655440001', 'Introduction to AI and Machine Learning', 'Learn the fundamentals of Artificial Intelligence and Machine Learning with hands-on projects and real-world applications. This comprehensive course covers neural networks, deep learning, and practical implementations.', '550e8400-e29b-41d4-a716-446655440001', 'BEGINNER', 'PUBLISHED', 'https://via.placeholder.com/400x250/6366f1/ffffff?text=AI+%26+ML', 99.99, 49.99, 120, 45, 1250, 4.8, 320, true, 'en-US', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('660e8400-e29b-41d4-a716-446655440002', 'Full Stack Web Development Bootcamp', 'Master modern web development with React, Node.js, Express, and MongoDB. Build and deploy production-ready full-stack applications with industry best practices and modern tools.', '550e8400-e29b-41d4-a716-446655440002', 'INTERMEDIATE', 'PUBLISHED', 'https://via.placeholder.com/400x250/10b981/ffffff?text=Full+Stack', 149.99, 79.99, 180, 72, 2100, 4.9, 580, true, 'en-US', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('660e8400-e29b-41d4-a716-446655440003', 'Data Science with Python', 'Dive deep into data analysis, visualization, and statistical modeling using Python, Pandas, NumPy, and Scikit-learn. Master the complete data science workflow from data collection to model deployment.', '550e8400-e29b-41d4-a716-446655440003', 'BEGINNER', 'PUBLISHED', 'https://via.placeholder.com/400x250/f59e0b/ffffff?text=Data+Science', 89.99, 44.99, 90, 38, 980, 4.7, 210, true, 'en-US', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('660e8400-e29b-41d4-a716-446655440004', 'Advanced Python Programming', 'Take your Python skills to the next level with advanced concepts including decorators, generators, context managers, metaclasses, and concurrent programming.', '550e8400-e29b-41d4-a716-446655440003', 'ADVANCED', 'PUBLISHED', 'https://via.placeholder.com/400x250/8b5cf6/ffffff?text=Advanced+Python', 129.99, 69.99, 60, 30, 650, 4.6, 145, true, 'en-US', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('660e8400-e29b-41d4-a716-446655440005', 'Mobile App Development with React Native', 'Build cross-platform mobile applications for iOS and Android using React Native. Learn navigation, state management, native modules, and app deployment.', '550e8400-e29b-41d4-a716-446655440002', 'INTERMEDIATE', 'PUBLISHED', 'https://via.placeholder.com/400x250/ec4899/ffffff?text=React+Native', 119.99, 59.99, 100, 50, 1420, 4.8, 267, true, 'en-US', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Add tags to courses (stored as JSON array in tags column)
UPDATE course SET tags = '["AI", "Machine Learning", "Python", "Data Science"]' WHERE id = '660e8400-e29b-41d4-a716-446655440001';
UPDATE course SET tags = '["React", "Node.js", "MongoDB", "Web Development", "Full Stack"]' WHERE id = '660e8400-e29b-41d4-a716-446655440002';
UPDATE course SET tags = '["Python", "Data Science", "Pandas", "Visualization", "Statistics"]' WHERE id = '660e8400-e29b-41d4-a716-446655440003';
UPDATE course SET tags = '["Python", "Advanced", "Programming", "Concurrency"]' WHERE id = '660e8400-e29b-41d4-a716-446655440004';
UPDATE course SET tags = '["React Native", "Mobile Development", "iOS", "Android", "Cross-Platform"]' WHERE id = '660e8400-e29b-41d4-a716-446655440005';

-- Add learning outcomes
UPDATE course SET learning_outcomes = '["Understand core AI and ML concepts", "Build neural networks from scratch", "Implement popular ML algorithms", "Deploy ML models to production"]' WHERE id = '660e8400-e29b-41d4-a716-446655440001';
UPDATE course SET learning_outcomes = '["Build full-stack web applications", "Master React and modern JavaScript", "Create RESTful APIs with Node.js", "Deploy apps to cloud platforms"]' WHERE id = '660e8400-e29b-41d4-a716-446655440002';
UPDATE course SET learning_outcomes = '["Analyze complex datasets", "Create data visualizations", "Build predictive models", "Apply statistical methods"]' WHERE id = '660e8400-e29b-41d4-a716-446655440003';
UPDATE course SET learning_outcomes = '["Master advanced Python features", "Write efficient and elegant code", "Implement design patterns", "Handle concurrent programming"]' WHERE id = '660e8400-e29b-41d4-a716-446655440004';
UPDATE course SET learning_outcomes = '["Build iOS and Android apps", "Implement navigation and routing", "Manage application state", "Publish apps to stores"]' WHERE id = '660e8400-e29b-41d4-a716-446655440005';

-- Add requirements
UPDATE course SET requirements = '["Basic Python programming", "Understanding of mathematics", "Familiarity with programming concepts"]' WHERE id = '660e8400-e29b-41d4-a716-446655440001';
UPDATE course SET requirements = '["HTML and CSS basics", "JavaScript fundamentals", "Basic command line knowledge"]' WHERE id = '660e8400-e29b-41d4-a716-446655440002';
UPDATE course SET requirements = '["Basic Python knowledge", "High school mathematics", "Curiosity about data"]' WHERE id = '660e8400-e29b-41d4-a716-446655440003';
UPDATE course SET requirements = '["Intermediate Python experience", "Object-oriented programming", "Basic software design knowledge"]' WHERE id = '660e8400-e29b-41d4-a716-446655440004';
UPDATE course SET requirements = '["JavaScript and React knowledge", "Understanding of mobile UX", "Basic Git usage"]' WHERE id = '660e8400-e29b-41d4-a716-446655440005';
