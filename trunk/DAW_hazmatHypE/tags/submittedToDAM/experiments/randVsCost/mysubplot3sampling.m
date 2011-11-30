function [] = mysubplot3sampling(A, B, Cupperlower, A_label, B_label, Cupperlower_label, titlename, filename)
figure()

% create 2D and 3D polygon for sampling box from Cupperlower:
X3 = [Cupperlower(1,1) Cupperlower(1,2) Cupperlower(1,3);
    Cupperlower(2,1) Cupperlower(1,2) Cupperlower(1,3);
    Cupperlower(2,1) Cupperlower(2,2) Cupperlower(1,3);
    Cupperlower(1,1) Cupperlower(2,2) Cupperlower(1,3);
    Cupperlower(1,1) Cupperlower(1,2) Cupperlower(2,3);
    Cupperlower(2,1) Cupperlower(1,2) Cupperlower(2,3);
    Cupperlower(2,1) Cupperlower(2,2) Cupperlower(2,3);
    Cupperlower(1,1) Cupperlower(2,2) Cupperlower(2,3)];

X3_d = X3(1:4, :);
X3_l = [X3(1,:); X3(4,:); X3(8,:); X3(5,:)];
X3_r = [X3(4,:); X3(3,:); X3(7:8,:)];

X_12 = X3(1:4,:);
X_13 = [X3(1:2,:); X3(6,:); X3(5,:)];
X_23 = [X3(1,:); X3(4,:); X3(8,:); X3(5,:)];

C = 'y';

subplot(2,2,1)
fill3(X3_d(:,1),X3_d(:,2),X3_d(:,3),C);
hold all; grid on;
fill3(X3_l(:,1),X3_l(:,2),X3_l(:,3),C);
fill3(X3_r(:,1),X3_r(:,2),X3_r(:,3),C);
plot3([X3(2,1); X3(6,1); X3(5,1)], [X3(2,2); X3(6,2); X3(5,2)], [X3(2,3); X3(6,3); X3(5,3)], '-k');
plot3(X3(6:7,1), X3(6:7,2), X3(6:7,3), '-k');
plot3(A(:,1),A(:,2),A(:,3), 'x');
plot3(B(:,1),B(:,2),B(:,3), 'o');
xlabel('cost')
ylabel('total risk')
zlabel('equity risk')
view([14.5 10]);
xlim([0.5e4 1.2e4]);

subplot(2,2,2)
fill(X_12(:,1),X_12(:,2),C);
hold all; grid on;
plot(A(:,1),A(:,2), 'x');
plot(B(:,1),B(:,2), 'o');
%ylim([4,6]*1e6);
h_legend = legend(Cupperlower_label, A_label, B_label);
set(h_legend,'FontSize',7);
xlabel('cost')
ylabel('total risk')


subplot(2,2,3)
fill(X_13(:,1),X_13(:,3),C);
hold all; grid on;
plot(A(:,1),A(:,3), 'x');
plot(B(:,1),B(:,3), 'o');
%ylim([2,5]*1e5);
h_legend = legend(Cupperlower_label, A_label, B_label);
set(h_legend,'FontSize',7);
xlabel('cost')
ylabel('equity risk')


subplot(2,2,4)
fill(X_23(:,2),X_23(:,3),C);
hold all; grid on;
plot(A(:,2),A(:,3), 'x');
plot(B(:,2),B(:,3), 'o');
%ylim([2,5]*1e5);
h_legend = legend(Cupperlower_label, A_label, B_label);
set(h_legend,'FontSize',7);
xlabel('total risk')
ylabel('equity risk')

% save plot as eps:
exportfig(gcf, filename);

% add title above all for plot on screen:
set(gcf,'NextPlot','add');
axes;
h = title(titlename);
set(gca,'Visible','off');
set(h,'Visible','on'); 


end
