
-- 기존 데이터 삭제 
DELETE FROM user_badges;
DELETE FROM user_section;
DELETE FROM user_course;
DELETE FROM course_completion;
DELETE FROM badge;

-- 1. 첫 걸음 (First Step)
INSERT INTO badge (id, name, description, icon, conditions, rarity) VALUES (1, '첫 걸음', '첫 번째 코스를 완주했습니다.', '🌱', '코스 1개 완주', 'COMMON');

-- 2. 갈맷길 마니아 (Galmaetgil Mania)
INSERT INTO badge (id, name, description, icon, conditions, rarity) VALUES (2, '갈맷길 마니아', '5개의 코스를 완주했습니다.', '🏆', '코스 5개 완주', 'EPIC');

-- 3. 리뷰어 (Reviewer)
INSERT INTO badge (id, name, description, icon, conditions, rarity) VALUES (3, '리뷰어', '첫 번째 리뷰를 작성했습니다.', '✍️', '첫 리뷰 작성', 'RARE');

-- 4. 장거리 트래커 (Long Distance Tracker)
INSERT INTO badge (id, name, description, icon, conditions, rarity) VALUES (4, '장거리 트래커', '총 50km 이상 걸었습니다.', '🏃', '누적 50km 달성', 'LEGENDARY');
