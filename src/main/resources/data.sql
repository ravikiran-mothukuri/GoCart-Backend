-- Insert default warehouses for major Indian cities
-- This file will be executed automatically by Spring Boot on startup

INSERT INTO warehouse (name, latitude, longitude) VALUES
-- Tier 1 Cities
('Mumbai Warehouse', 19.0760, 72.8777),
('Delhi Warehouse', 28.7041, 77.1025),
('Bangalore Warehouse', 12.9716, 77.5946),
('Hyderabad Warehouse', 17.3850, 78.4867),
('Chennai Warehouse', 13.0827, 80.2707),
('Kolkata Warehouse', 22.5726, 88.3639),
('Pune Warehouse', 18.5204, 73.8567),
('Ahmedabad Warehouse', 23.0225, 72.5714),

-- Tier 2 Cities
('Jaipur Warehouse', 26.9124, 75.7873),
('Lucknow Warehouse', 26.8467, 80.9462),
('Kanpur Warehouse', 26.4499, 80.3319),
('Nagpur Warehouse', 21.1458, 79.0882),
('Indore Warehouse', 22.7196, 75.8577),
('Thane Warehouse', 19.2183, 72.9781),
('Bhopal Warehouse', 23.2599, 77.4126),
('Visakhapatnam Warehouse', 17.6868, 83.2185),
('Patna Warehouse', 25.5941, 85.1376),
('Vadodara Warehouse', 22.3072, 73.1812),
('Ghaziabad Warehouse', 28.6692, 77.4538),
('Ludhiana Warehouse', 30.9010, 75.8573),
('Agra Warehouse', 27.1767, 78.0081),
('Nashik Warehouse', 19.9975, 73.7898),
('Faridabad Warehouse', 28.4089, 77.3178),
('Meerut Warehouse', 28.9845, 77.7064),
('Rajkot Warehouse', 22.3039, 70.8022),
('Varanasi Warehouse', 25.3176, 82.9739),
('Srinagar Warehouse', 34.0837, 74.7973),
('Amritsar Warehouse', 31.6340, 74.8723),

-- South India
('Coimbatore Warehouse', 11.0168, 76.9558),
('Kochi Warehouse', 9.9312, 76.2673),
('Thiruvananthapuram Warehouse', 8.5241, 76.9366),
('Mysore Warehouse', 12.2958, 76.6394),
('Mangalore Warehouse', 12.9141, 74.8560),
('Vijayawada Warehouse', 16.5062, 80.6480),

-- North East
('Guwahati Warehouse', 26.1445, 91.7362),
('Imphal Warehouse', 24.8170, 93.9368),

-- Central India
('Raipur Warehouse', 21.2514, 81.6296),
('Jabalpur Warehouse', 23.1815, 79.9864),

-- Others
('Chandigarh Warehouse', 30.7333, 76.7794),
('Dehradun Warehouse', 30.3165, 78.0322),
('Jammu Warehouse', 32.7266, 74.8570),
('Shimla Warehouse', 31.1048, 77.1734),
('Ranchi Warehouse', 23.3441, 85.3096),
('Bhubaneswar Warehouse', 20.2961, 85.8245),
('Goa Warehouse', 15.2993, 74.1240),
('Puducherry Warehouse', 11.9416, 79.8083)
ON DUPLICATE KEY UPDATE name=name;
