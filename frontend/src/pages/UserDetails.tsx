import {useUsers} from "../hooks/userUsers";
import {useNavigate, useParams} from "react-router-dom";
import {useState} from "react";
import {ShieldCheck, UserCircle, Users,Lock} from "lucide-react";
import UserBasicDetails from "../components/Users/UserBasicDetails.tsx";
import UserSecurityDetails from "../components/Users/UserSecurityDetails";

const UserDetailsPage: React.FC = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const { users, loading } = useUsers();
    const [currentSection, setCurrentSection] = useState('details');
    const user = users.find(u => u.id === Number(id));

    if (loading) return <div>Loading...</div>;
    if (!user) return <div>User not found</div>;

    const navigationItems = [
        { name: 'Details', path: 'details', icon: <UserCircle className="w-5 h-5" /> },
        { name: 'Security', path: 'security', icon: <ShieldCheck className="w-5 h-5" /> },
        { name: 'Groups', path: 'groups', icon: <Users className="w-5 h-5" /> },
        { name: 'Permissions', path: 'permissions', icon: <Lock className="w-5 h-5" /> },
    ];

    const renderContent = () => {
        switch(currentSection) {
            case 'details':
                return <UserBasicDetails user={user} />;
            case 'security':
                return <UserSecurityDetails user={user}/>;
            case 'groups':
                return <div></div>;
            case 'permissions':
                return <div></div>;
            default:
                return <UserBasicDetails user={user} />;
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <nav className="bg-white border-b border-gray-200">
                <div className="px-4">
                    <div className="flex h-16 items-center justify-between">
                        <div className="flex space-x-8">
                            {navigationItems.map((item) => (
                                <button
                                    key={item.path}
                                    onClick={() => setCurrentSection(item.path)}
                                    className={`inline-flex items-center px-1 pt-1 text-sm font-medium 
                                        ${currentSection === item.path
                                        ? 'border-b-2 border-indigo-500 text-gray-900'
                                        : 'text-gray-500 hover:border-gray-300 hover:text-gray-700'}`}
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
        <dt className="text-sm font-medium text-gray-500">{label}</dt>
        <dd className="mt-1 text-sm text-gray-900">{value}</dd>
    </div>
);

export default UserDetailsPage;