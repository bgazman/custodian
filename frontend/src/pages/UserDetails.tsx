import { useGetUser, useGetUserAccess, useGetUserProfile } from "../api/generated/user-controller/user-controller";
import { useNavigate, useParams } from "react-router-dom";
import { useState } from "react";
import { ShieldCheck, UserCircle, Users, Lock } from "lucide-react";
import UserBasicDetails from "../components/Users/UserBasicDetails.tsx";

const UserDetailsPage: React.FC = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const { data: user, isLoading } = useGetUser(Number(id));
    const [currentSection, setCurrentSection] = useState('details');

    if (isLoading) return <div>Loading...</div>;
    if (!user) return <div>User not found</div>;

    const navigationItems = [
        { name: 'Details', path: 'details', icon: <UserCircle className="w-5 h-5" /> },
        { name: 'Security', path: 'security', icon: <ShieldCheck className="w-5 h-5" /> },
        { name: 'Groups', path: 'groups', icon: <Users className="w-5 h-5" /> },
        { name: 'Permissions', path: 'permissions', icon: <Lock className="w-5 h-5" /> },
    ];

    const renderContent = () => {
        switch (currentSection) {
            case 'details':
                return <UserBasicDetails user={user} />;
            case 'security':
                return <UserSecurityDetails user={user} />;
            case 'groups':
                return <UserGroups user={user} />;
            case 'permissions':
                return <div>Permissions section under construction</div>;
            default:
                console.warn(`Invalid section: ${currentSection}. Rendering default section.`);
                return <UserBasicDetails user={user} />;
        }
    };


    return (
        <div className="min-h-screen bg-background">
            <nav className="bg-background border-b border-border">
                <div className="px-4">
                    <div className="flex h-16 items-center justify-between">
                        <div className="flex space-x-8">
                            {navigationItems.map((item) => (
                                <button
                                    key={item.path}
                                    onClick={() => setCurrentSection(item.path)}
                                    className={`inline-flex items-center px-1 pt-1 text-sm font-medium 
                                        ${currentSection === item.path
                                        ? 'border-b-2 border-primary text-text-dark'
                                        : 'text-text-muted hover:border-border hover:text-text'}`}
                                >
                                    <span className="mr-2">{item.icon}</span>
                                    {item.name}
                                </button>
                            ))}
                        </div>
                    </div>
                </div>
            </nav>

            <main className="p-6">
                {renderContent()}
            </main>
        </div>
    );
};



const InfoItem = ({ label, value }: { label: string; value: string }) => (
    <div>
        <dt className="text-sm font-medium text-text-muted">{label}</dt>
        <dd className="mt-1 text-sm text-text-dark">{value}</dd>
    </div>
);

export default UserDetailsPage;